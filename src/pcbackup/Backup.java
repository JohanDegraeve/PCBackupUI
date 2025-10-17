/*
 * Copyright 2024 Johan Degraeve
 *
 * This file is part of PCBackup.
 *
 * PCBackup is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PCBackup is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PCBackup. If not, see <https://www.gnu.org/licenses/>.
 */
package pcbackup;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.fasterxml.jackson.databind.ObjectMapper;

import model.AFileOrAFolder;
import model.AFolder;
import model.CommandLineArguments;
import model.Constants;
import utilities.CreateFullBackup;
import utilities.CreateSubFolder;
import utilities.FileAndFolderUtilities;
import utilities.ListBackupsInFolder;
import utilities.OtherUtilities;
import utilities.WriteToFile;

public class Backup implements Runnable {

	private static CommandLineArguments commandLineArguments;
	
	private static void backup(CommandLineArguments commandLineArguments) {
		
		/**
		 * where to find the source files
		 */
        Path sourceFolderPath = Paths.get(commandLineArguments.source);
        
        /**
         * main path for backup, this is the backup folder path without the specific folder (ie without '2023-12-06 18;24;41 (Full)' or anything like that)
         */
        Path destinationFolderPath = Paths.get(commandLineArguments.destination);

        /**
         * path to previous most recent backup, either Full or Incremental. 
         */
    	Path mostRecentBackupPath = null;
    	// Get it now, if it exists before we create the new backup folder
    	try {
    		mostRecentBackupPath = ListBackupsInFolder.getMostRecentBackup(destinationFolderPath);
		} catch (IOException e) {
			e.printStackTrace();
			commandLineArguments.processText.process("Exception in main, while getting list of backups");
			commandLineArguments.processText.process(e.toString());
            Thread.currentThread().interrupt();
            return;
		}
    	if (mostRecentBackupPath == null && !commandLineArguments.fullBackup) {
    		commandLineArguments.processText.process("You're asking an incremental backup but there's no previous backup. Start with a full backup or check the destination folder."); 
    		Thread.currentThread().interrupt();
    		return;
    	} else if (mostRecentBackupPath != null && !commandLineArguments.fullBackup) {
    		commandLineArguments.processText.process("Latest backup = " + mostRecentBackupPath.toString() + ". Only the new or modified files and folders since this latest backup will be copied.");
    	}


        // create backupfoldername for the backup, this folder will be created within destination folder
        // example for full backup : '2023-12-06 18;24;41 (Full)'
        // example for incremental backup : '2023-12-28 17;07;13 (Incremental)'
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.BACKUPFOLDERDATEFORMAT_STRING);
        /**
         * name of the folder within destinationFolderPath, where backup will be placed<br>
         * So if we have destinationFolderPath, then destinationFolderPath/backupfoldername will contain the copied files and folders
         */
		String backupfoldername = dateFormat.format(new Date());
        if (commandLineArguments.fullBackup) {
        	backupfoldername = backupfoldername + " (Full)";
        } else {
        	backupfoldername = backupfoldername + " (Incremental)";
        }
        
        /**
         * where to write the backup, this includes the backupfoldername, like '2023-12-06 18;24;41 (Full)' or '2023-12-28 17;07;13 (Incremental)'
         */
        Path destinationFolderPathSubFolder = CreateSubFolder.createSubFolder(commandLineArguments.destination, backupfoldername, commandLineArguments);
        
        commandLineArguments.processText.process("New backup folder created: " + backupfoldername);
        commandLineArguments.processText.process("Reading all files and folders in the source and building the folder structure");// in other words create an instance of AFolder

    	// first we make a list of files and folder in the sourceFolderPath,
        // for each file or folder we create an instance of AFileOrAFolder
        // Create a DirectoryStream to iterate over the contents of the sourceFolderPath
        // we create an instance of AFolder to hold the list of files and folders in the source
        //    although we don't really need the source folder
        //    this is is done because otherwise the json deserialisation doesn't work
        AFolder listOfFilesAndFoldersInSourceFolder = new AFolder(sourceFolderPath.toString(), "");
        
        try {
        	
    		/**
    		 * FOLLOWING CODE REPEATS IN FILEANDFOLDERUTILITIES.JAVA, function createAFileOrAFolder, If you're changing anything here, check if the same change is necessary in BACKUP.JAVA
    		 */
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(sourceFolderPath)) {
            	
                directoryLoop: for (Path path : directoryStream) {
                	
                	if (!(Files.isDirectory(path))) {
                		// actually here it should always be a directory, if it's not then it should be a file to exclude
                		// because we always start with a directory
                		// anyway let's check if the file is in the list of files to exclude, example .DS_Store
                		if (commandLineArguments.excludedFiles.contains(path.getFileName().toString())) {
                    		continue;
                		}
                		// check if the file is of format .849C9593-D756-4E56-8D6E-42412F2A707B seems a Microsoft hidden file
                		if (OtherUtilities.fileNeedsToBeIgnored(path.getFileName().toString())) {
                			continue;
                		}
                	} else {
                		// check if folder is in excludedPaths
                	
                		/*String pathString = path.getFileName().toString();
                		if (!pathString.contains("XDrive - Werking")) {
                			continue;
                		}
                		if (pathString.contains("XDrive - Werking_2021-2025")) {
                			continue;
                		}
                		if (pathString.contains("XDrive Archieven - Documents")) {
                			continue;
                		}*/
                		
                		for (String excludedPath : commandLineArguments.excludedPaths) {
                			if (path.getFileName().toString().trim().equals(excludedPath.trim())) {
                				commandLineArguments.processText.process("      Excluding folder '" + excludedPath + "' because " + excludedPath + " is in the file excludedpathlist");
                				continue directoryLoop;
                			}
                		}
                		
                	}
                	
                	if (Files.isDirectory(path)) {
                		commandLineArguments.processText.process("   Reading files in folder \"" + path.getFileName().toString() + "\"");
                	}
                	
                	listOfFilesAndFoldersInSourceFolder.getFileOrFolderList().add(FileAndFolderUtilities.createAFileOrAFolder(path, backupfoldername, commandLineArguments.excludedFiles, commandLineArguments.excludedPaths, commandLineArguments.addpathlengthforallfolders, commandLineArguments.processText, commandLineArguments));
                	
                }
                
            }

        } catch (IOException e) {
            e.printStackTrace();
            commandLineArguments.processText.process("Exception in main, while creating list of folders");
            commandLineArguments. processText.process(e.toString());
            Thread.currentThread().interrupt();
            return;
        }
        
        //if option is F, then create full backup
        if (commandLineArguments.fullBackup) {
        	commandLineArguments.processText.process("Starting full backup");
            CreateFullBackup.createFullBackup(listOfFilesAndFoldersInSourceFolder, sourceFolderPath, destinationFolderPathSubFolder, commandLineArguments);
            commandLineArguments.processText.process("Backup finished");
            commandLineArguments.processText.process("");
            commandLineArguments.processText.process("========================================================");
            commandLineArguments.processText.process("");
            Thread.currentThread().interrupt();
            return;
        } else {
        	
        	commandLineArguments.processText.process("Parsing the json file from previous backup " + mostRecentBackupPath.resolve("folderlist.json").toString()); 
        	commandLineArguments.processText.process("   "); 
            // convert folderlist.json in most recent backup path to AFileOrAFolder
            AFileOrAFolder listOfFilesAndFoldersInPreviousBackupFolder = FileAndFolderUtilities.fromFolderlistDotJsonToAFileOrAFolder(mostRecentBackupPath.resolve("folderlist.json"), commandLineArguments.processText);
            
            commandLineArguments.processText.process("Starting incremental backup");
            
            // we know for sure that both listOfFilesAndFoldersInSourceFolder and listOfFilesAndFoldersInPreviousBackupFolder are instance of AFolder
            // let's check anyway
            if (!(listOfFilesAndFoldersInSourceFolder instanceof AFolder)) {commandLineArguments.processText.process("listOfFilesAndFoldersInSourceFolder is not an instance of AFolder");Thread.currentThread().interrupt();return;} 
            if (!(listOfFilesAndFoldersInPreviousBackupFolder instanceof AFolder)) {commandLineArguments.processText.process("listOfFilesAndFoldersInPreviousBackupFolder is not an instance of AFolder");Thread.currentThread().interrupt();return;}
            // set the name of the first folder to "", because this may be the original main folder name which we don't need
            listOfFilesAndFoldersInSourceFolder.setName("");
            listOfFilesAndFoldersInPreviousBackupFolder.setName("");
            FileAndFolderUtilities.compareAndUpdate(listOfFilesAndFoldersInSourceFolder, listOfFilesAndFoldersInPreviousBackupFolder, sourceFolderPath, destinationFolderPathSubFolder, new ArrayList<String>(), backupfoldername, 1, commandLineArguments);
            
    		// do the foldername mapping
    		OtherUtilities.doFolderNameMapping((AFolder)listOfFilesAndFoldersInPreviousBackupFolder, commandLineArguments, destinationFolderPath.resolve(backupfoldername), commandLineArguments.processText);
            
    		// store folderlist.json on disk
    		try {
    			
    			commandLineArguments.processText.process("Writing folderlist.json to " + destinationFolderPathSubFolder.toString());
    			
        		// write the json file to the destination folder
        		WriteToFile.writeToFile((new ObjectMapper()).writeValueAsString(listOfFilesAndFoldersInPreviousBackupFolder), destinationFolderPathSubFolder.toString() + File.separator + "folderlist.json");
            	
            } catch (IOException e) {
            	commandLineArguments.processText.process("Failed to write json file folderlist.json to  " + destinationFolderPath.toString());
    			Thread.currentThread().interrupt();return;
            }
    		
    		// store folderlist-withfullpaths.json. This json file has the same contents, but the 'name' is the full path of a file or folder. Makes it easier to find it in the backup folder.
    		try {
    			
    			commandLineArguments.processText.process("Writing folderlist-withfullpaths.json to " + destinationFolderPathSubFolder.toString());
    			
    			String jsonString = (new ObjectMapper()).writeValueAsString(FileAndFolderUtilities.createAFileOrAFolderWithFullPath(listOfFilesAndFoldersInPreviousBackupFolder, new ArrayList<>(), null));

    	       	// write the json file to the destination folder
        		WriteToFile.writeToFile(OtherUtilities.removeDoubleBackSlashes(jsonString), destinationFolderPathSubFolder.toString() + File.separator + "folderlist-withfullpaths.json");
            	
            } catch (IOException e) {
            	commandLineArguments.processText.process("Failed to write json file folderlist-withfullpaths.json to  " + destinationFolderPath.toString());
    			Thread.currentThread().interrupt();return;
            }
    		
    		commandLineArguments.processText.process("Backup finished");
            commandLineArguments.processText.process("");
            commandLineArguments.processText.process("========================================================");
            commandLineArguments.processText.process("");
            
            Thread.currentThread().interrupt();return;
            
        }
        

	}

	public Backup(CommandLineArguments commandLineArguments) {
		Backup.commandLineArguments = commandLineArguments;
	}
	
	@Override
	public void run() {
		Backup.backup(commandLineArguments);
	}
	
}
