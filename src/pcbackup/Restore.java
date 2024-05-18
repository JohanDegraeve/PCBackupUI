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

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.List;

import Interfaces.ProcessText;
import model.AFile;
import model.AFileOrAFolder;
import model.AFolder;
import model.CommandLineArguments;
import model.Constants;
import utilities.FileAndFolderUtilities;
import utilities.ListBackupsInFolder;
import utilities.Logger;
import utilities.OtherUtilities;
import utilities.PathUtilities;

public class Restore {

	public static void restore(CommandLineArguments commandLineArguments) {

		/**
		 * where to find the backup files, this is the backup folder path without the specific folder (ie without '2023-12-06 18;24;41 (Full)' or anything like that)
		 */
        Path sourceFolderPath = Paths.get(commandLineArguments.destination);
        
        /**
         * where to restore the files
         */
        Path destinationFolderPath = Paths.get(commandLineArguments.restoreto);

        try {
        	
			String latestBackupFolderName = ListBackupsInFolder.getMostRecentBackup(sourceFolderPath, commandLineArguments.restoreDate);
			
			if (latestBackupFolderName == null) {
                commandLineArguments.processText.process("No backups are found that were created before or at " + (new SimpleDateFormat(Constants.OUTPUTDATEFORMAT_STRING)).format(commandLineArguments.restoreDate));
                Thread.currentThread().interrupt();
			}
			
			commandLineArguments.processText.process("Found backup " + latestBackupFolderName + " created before or at " + (new SimpleDateFormat(Constants.OUTPUTDATEFORMAT_STRING).format(commandLineArguments.restoreDate)));
			
			// get list of all older backup folders, to support the case were we don't find a specific file in the specified folder, we can start searching in older backups,
			List<String> olderBackups = ListBackupsInFolder.getAllBackupFoldersAsStrings(sourceFolderPath, latestBackupFolderName);
			
			// get the file folderlist.json as AFileOrAFolder
			Path pathWithJsonFile = sourceFolderPath.resolve(latestBackupFolderName).resolve("folderlist.json");
			commandLineArguments.processText.process("Parsing " + pathWithJsonFile.toString());
			AFileOrAFolder listOfFilesAndFoldersInLastBackup = FileAndFolderUtilities.fromFolderlistDotJsonToAFileOrAFolder(pathWithJsonFile, commandLineArguments.processText);
			
			if (listOfFilesAndFoldersInLastBackup instanceof AFolder) {
				
		    	// if subfolderToRestore is specified, then we need to search within folderToBackup for an instance of AFileOrAFolder that matches that subfolder
		    	AFolder folderToStart =  getSubFolderAsAFolder((AFolder)listOfFilesAndFoldersInLastBackup, Paths.get(commandLineArguments.subfolderToRestore), commandLineArguments.processText);

		    	commandLineArguments.processText.process("Restoring folders and files ...");
				restore(folderToStart, destinationFolderPath, sourceFolderPath, Paths.get(commandLineArguments.subfolderToRestore), olderBackups, commandLineArguments, 1);
			} else {
				commandLineArguments.processText.process("First element in folderlist.json is not a folder, looks like a coding error");
			}
			
			commandLineArguments.processText.process("Restore finished, see " + destinationFolderPath.toString());

			commandLineArguments.processText.process("");
            commandLineArguments.processText.process("========================================================");
            commandLineArguments.processText.process("");
            Thread.currentThread().interrupt();

		} catch (IOException e) {
			commandLineArguments.processText.process("Exception in restore");
            commandLineArguments.processText.process(e.toString());
            Thread.currentThread().interrupt();
		}
        
	}
	
	/**
	 * reads folderToBackup, 
	 * - goes through the folders and files one by one recursively
	 * - folders will be created in the destination
	 * - files are copied from the correct original backup and restores
	 * @param folderToBackup instance of AFolder to backup
	 * @param destinationFolder where to copy to, this is for example c:\restorefolder. destinationFolder is an absolute Path
	 * @param sourceBackupRootFolder this is for example c:\backupfolder without the name of the incremental or full folder. sourceBackupRootFolder is an absolute Path
	 * @param subfolder that is being restored, will be used as subfolder in the sourceBackupRootFolder where to find the original file, subfolder is a relative path
	 * @param olderBackups backups older than the restoredata, this is just a list of strings, specifying the backup name (eg 2024-03-10 18;11;35 (Incremental). This list is used in case a file is not found in the backup where it should be found (due to a deletion of a file by error for example)
	 * @param level : if called recursively, this is tells us if it's the first time or more that the function is called
	 */
    private static void restore(AFolder folderToBackup, Path destinationFolder, Path sourceBackupRootFolder, Path subfolder, List<String> olderBackups, CommandLineArguments commandLineArguments, int level) {
    	
    	// we must apply the foldername mapping in reverse order
    	// the first folder in subfolder is the one where we need to apply the mapping
    	Path[] paths = PathUtilities.splitPath(subfolder);
    	Path newDestinationFolder = destinationFolder;
    	for (int i = 0;i < paths.length; i++) {
    		if (i == 0) {
    			newDestinationFolder = newDestinationFolder.resolve(OtherUtilities.getKeyForValue(commandLineArguments.folderNameMapping, paths[i].getFileName().toString()));
    		} else {
    			newDestinationFolder = newDestinationFolder.resolve(paths[i].getFileName().toString());
    		}
    	}
    	
    	for (AFileOrAFolder sourceItem : folderToBackup.getFileOrFolderList()) {
    		
    		if (sourceItem instanceof AFolder) {
    			
    			Path folderToCreate = newDestinationFolder;
    			if (level == 1) {
    				folderToCreate = newDestinationFolder.resolve(OtherUtilities.getKeyForValue(commandLineArguments.folderNameMapping, sourceItem.getName()));	
    			} else {
    				folderToCreate = newDestinationFolder.resolve(sourceItem.getName());
    			}
    			
    			// create the folder in the destination and call restore for the folder recursively
    			try {
    				
					Files.createDirectories(folderToCreate);
					
					Restore.restore((AFolder)sourceItem, destinationFolder, sourceBackupRootFolder, subfolder.resolve(sourceItem.getName()), olderBackups, commandLineArguments, level + 1);
					
				} catch (IOException e) {
					e.printStackTrace();
					commandLineArguments.processText.process("Exception in restore, while creating the directory " + folderToCreate.getFileName().toString());
		            commandLineArguments.processText.process(e.toString());
		            Thread.currentThread().interrupt();
				}
    			
    		} else {
    			
    			Path sourceToCopy = sourceBackupRootFolder.resolve(sourceItem.getPathToBackup()).resolve(subfolder).resolve(sourceItem.getName());
    			Path destination = newDestinationFolder.resolve(sourceItem.getName());
    			
    			try {
    				
    				if (!Files.isDirectory(destination)) {// it should not be a directory but let's check anyway
    					if (!Files.exists(destination.getParent())) { // check if parent directory exists, if not created it
    						Files.createDirectories(destination.getParent());
    					}
    				}
    				
					copyFile(sourceToCopy, destination, commandLineArguments);
					
				} catch (NoSuchFileException e) {
					commandLineArguments.processText.process("   NoSuchFileException occurred while trying to copy " + sourceToCopy.toString() + " to " + destination.toString()) ;
					commandLineArguments.processText.process("      Will try to find the source in older backups, but there might be another error also");
					String olderBackup = tryToFindInOlderBackups((AFile)sourceItem, subfolder, olderBackups, sourceBackupRootFolder);
					if (olderBackup == null) {
						commandLineArguments.processText.process("      Did not find the missing file in previous backups");
					} else {
						commandLineArguments.processText.process("      Found the missing file in backup \"" + olderBackup + "\"");
						try {
							sourceToCopy = sourceBackupRootFolder.resolve(olderBackup).resolve(subfolder).resolve(sourceItem.getName());
							copyFile(sourceToCopy, destination, commandLineArguments);
							commandLineArguments.processText.process("      and successfully copied to restore folder");
						} catch (FileAlreadyExistsException e2) {
							commandLineArguments.processText.process("The file " + sourceToCopy.toString() + " already exists in the destination folder");
							commandLineArguments.processText.process("If you want to restore with overwrite, add the optional argument --overwrite=true");
							commandLineArguments.processText.process("Restore interrupted");
				            Thread.currentThread().interrupt();
						} catch (IOException e1) {
							commandLineArguments.processText.process("      copy failed again. Exception occurred : ");
							commandLineArguments.processText.process(e1.toString());
						}
					}
				} catch (FileAlreadyExistsException e) {
					commandLineArguments.processText.process("The file " + sourceToCopy.toString() + " already exists in the destination folder");
					commandLineArguments.processText.process("If you want to restore with overwrite, add the optional argument --overwrite=true");
					commandLineArguments.processText.process("Restore interrupted");
		            Thread.currentThread().interrupt();
				} catch (IOException e) {
					commandLineArguments.processText.process("Exception in restore, while copying the file " + sourceToCopy.toString() + " to " + destination.toString());
		            commandLineArguments.processText.process(e.toString());
		            Thread.currentThread().interrupt();
				}
    		}
    		
    	}
    }
    
    private static void copyFile(Path source, Path dest, CommandLineArguments commandLineArguments) throws IOException {
    	if (commandLineArguments.overwrite) {
    		Files.copy(source, dest, StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
    	} else {
    		Files.copy(source, dest, StandardCopyOption.COPY_ATTRIBUTES);
    	}
    	
    }
    
    /**
     * tries to find the sourceItem in older backups, backups older than backupfolder specified in sourceItem<br>
     * does not copy, just returns the backup foldername
     * @param sourceItem
     * @param subfolder
     * @param olderBackups
     * @return null if not found, the olderBackup name if found
     */
    private static String tryToFindInOlderBackups(AFile sourceItem, Path subfolder, List<String> olderBackups, Path sourceBackupRootFolder) {
    	
    	for (String olderBackup: olderBackups) {
    		
    		if (sourceItem.getPathToBackup().compareTo(olderBackup) < 0) {
    			continue;
    		}
    		
    		Path pathToSearch = sourceBackupRootFolder.resolve(olderBackup).resolve(subfolder).resolve(sourceItem.getName());
    		
    		if (Files.exists(pathToSearch)) {
    			return olderBackup;
    		}
    		
    	}
    	
    	return null;
    	
    }
    
    /**
     * if user specified a foldertorestore, then we need to find the corresponding instance of AFileOrAFolder within folderToSearchIn
     * @param folderToSearchIn
     * @param subfolder
     * @return the folder
     */
    private static AFolder getSubFolderAsAFolder(AFolder folderToSearchIn, Path subfolder, ProcessText processText) {
    	
    	// split subfolder, because it can be a concatentation of paths, in other words split by "/" or "\" (depending on os)
    	Path[] subfolders = PathUtilities.splitPath(subfolder);
    	int subfoldersCounter = 0;
    	
    	AFolder deeperAFileOrAFolder = folderToSearchIn;
    	
    	while (subfoldersCounter < subfolders.length) {

    		// if subfolder is an empty string, then actually no subfolder is specified
    		// in this case, the loop exist immediately
    		if (subfolders[subfoldersCounter].toString().length() == 0) {
    			break;
    		}
    		
        	// find the instance of AFileOrAFolder in folderToSearchIn list (ie in the list of files and folders contained within folderToSearchIn)
        	AFileOrAFolder folderFound = FileAndFolderUtilities.findMatchingItem(new AFolder(subfolders[subfoldersCounter].toString(), ""), deeperAFileOrAFolder.getFileOrFolderList());
        	
        	if (folderFound == null) {
        		processText.process("You specified " + subfolder + " as subfoldertorestore, but it does not exist in backup.");
                Thread.currentThread().interrupt();
        	}
        	
        	// folderFound must be a directory, if not, it's a file, and user made some mistake
        	if (folderFound instanceof AFile) {
        		processText.process("You specified " + subfolder + " as subfoldertorestore, but this seems to be a file, not a folder");
                Thread.currentThread().interrupt();
        	}

        	deeperAFileOrAFolder = (AFolder)folderFound;

        	subfoldersCounter++;
        	
    	}
    	
    	return deeperAFileOrAFolder;   	
    	
    }
	
}
