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
package utilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import model.AFile;
import model.AFileOrAFolder;
import model.AFolder;
import model.CommandLineArguments;

public class CreateFullBackup {

	/**
	 * create a full backup
	 * @param listOfFilesAndFoldersInSourceFolder : files and folders in sourceFolderPath in a list of AFileOrAFolder
	 * @param sourceFolderToJson : json formatted file and folder list
	 * @param destinationFolderPath : must exist
	 */

	public static void createFullBackup(AFolder listOfFilesAndFoldersInSourceFolder, Path sourceFolderPath, Path destinationFolderPath, CommandLineArguments commandLineArguments) {
		
		// check if destination path already exists, otherwise stop, coding error
		if (!(Files.exists(destinationFolderPath))) {
			Logger.log("in createFullBackup, folder " + destinationFolderPath.toString() + " does not exist, looks like a coding error");
			System.exit(1);
		}
		
		// copy files that are in aFileOrAFolderSourceFolder
		copyFilesAndFoldersFromSourceToDest(listOfFilesAndFoldersInSourceFolder.getFileOrFolderList(), sourceFolderPath, destinationFolderPath, true);
		
		// do the foldername mapping
		OtherUtilities.doFolderNameMapping(listOfFilesAndFoldersInSourceFolder, commandLineArguments, destinationFolderPath);

		// store folderlist.json on disk
		try {
    		// write the json file to destination folder
    		WriteToFile.writeToFile((new ObjectMapper()).writeValueAsString(listOfFilesAndFoldersInSourceFolder), destinationFolderPath.toString() + File.separator + "folderlist.json");
        	
        } catch (IOException e) {
        	Logger.log("Failed to write json file folderlist.json to  " + destinationFolderPath.toString());
			System.exit(1);
        }

		System.out.println("Backup finished, see " + destinationFolderPath.toString());

	}
	
    public static Path getMostRecentBackup(Path backupFolder) throws IOException {
        List<Path> backupFolders = getAllBackupFolders(backupFolder);
        return Collections.max(backupFolders);
    }

	private static void copyFilesAndFoldersFromSourceToDest(List<AFileOrAFolder> listOfFilesAndFoldersInSourceFolder, Path sourceFolderPath, Path destinationFolderPath, boolean createEmptyFolders) {

		for (AFileOrAFolder aFileOrAFolder: listOfFilesAndFoldersInSourceFolder) {
			
			// add folder or filename to source and destination folders
			Path sourcePathToCopyFrom = sourceFolderPath.resolve(aFileOrAFolder.getName());
			Path destinationPathToCopyTo = destinationFolderPath.resolve(aFileOrAFolder.getName());
			

			if (aFileOrAFolder instanceof AFile) {

				// we need to copy the file from source to dest
				
				try {
					
					// no need to create the subfolder here because, as we create all (even empty) folders in the full backup, it's already there
					
					Files.copy(sourcePathToCopyFrom, destinationPathToCopyTo, StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
					
				} catch (IOException e) {
					
					e.printStackTrace();
					
					Logger.log("Exception occurred while copying from " + sourcePathToCopyFrom.toString() + " to " + destinationPathToCopyTo.toString());
					
					System.exit(1);
					
				}
				
			} else if (aFileOrAFolder instanceof AFolder){
				
				if (createEmptyFolders) {
					// check if destinationPathToCopyTo exists, if not created it
					createSubFolderIfNotExisting(destinationPathToCopyTo);
				}
				
				AFolder afolder = (AFolder)aFileOrAFolder;
				
				copyFilesAndFoldersFromSourceToDest(afolder.getFileOrFolderList(), sourceFolderPath.resolve(afolder.getName()), destinationFolderPath.resolve(afolder.getName()), createEmptyFolders);
				
			} else {
				
				// that's a coding error
				Logger.log("error in copyFilesAndFoldersFromSourceToDest, a AFileOrAFolder that is not AFile and not AFolder ...");
				System.exit(1);
			}

			
		}
		
	}
	
	private static void createSubFolderIfNotExisting(Path path) {
		
		// check if destinationPathToCopyTo exists
		if (!(Files.exists(path, LinkOption.NOFOLLOW_LINKS))) {
			
		    // Create the subfolder
		    File subfolder = new File(path.toString());

		    if (subfolder.mkdirs()) {
		        Logger.log("in createSubFolderIfNotExisting, backup folder created successfully: " + subfolder.getAbsolutePath());
		    } else {
		    	Logger.log("in createSubFolderIfNotExisting, Subfolder already exists or creation failed: " + subfolder.getAbsolutePath());
		    }
			
		}

	}
	
    private static List<Path> getAllBackupFolders(Path backupFolder) throws IOException {
        List<Path> backupFolders = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(backupFolder, entry -> Files.isDirectory(entry) && isValidBackupFolder(entry))) {
            for (Path entry : directoryStream) {
                backupFolders.add(entry);
            }
        }
        return backupFolders;
    }

    private static boolean isValidBackupFolder(Path folder) {
        String folderName = folder.getFileName().toString();
        return folderName.matches("\\d{4}-\\d{2}-\\d{2} \\d{2};\\d{2};\\d{2} (Full|Incremental)");
    }
}
