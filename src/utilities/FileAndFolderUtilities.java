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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import Interfaces.ProcessText;
import model.AFileOrAFolder;
import model.AFileOrAFolderForFullPath;
import model.AFileWithLastModified;
import model.AFolder;
import model.AFolderWithFullPath;
import model.CommandLineArguments;
import model.Constants;
import model.AFile;

/**
 * utilities related to creation and processing of instances of type AFile and AFileOrAFolder
 */
public class FileAndFolderUtilities {

    /**
     * creates an instance of AFileOrAFolder for folderPath.<br>
     * @param folderOrStringPath can be path to a folder or a file
     * @param backupFolderName just a foldername of the full or incremental backup where to find the file, example '2024-01-12 16;46;55 (Full)' This is actually not used just stored in an instance of AFile (not if it's a folder) 
     * @return an instance of either a folder or a file
     * @param excludefilelist array of strings that should be ignored as filename
     * @param excludedpathlist array of strings that should be ignored as foldername
     * @param addpathlength for testing only
     * @throws IOException
     */
    public static AFileOrAFolder createAFileOrAFolder(Path folderOrStringPath, String backupFolderName, List<String> excludefilelist, List<String> excludedpathlist, boolean addpathlength, ProcessText processText) throws IOException {

    	String fileOrFolderNameWithoutFullPath = folderOrStringPath.getFileName().toString();

    	if (Files.isDirectory(folderOrStringPath)) {
    		
    		AFolder returnValue = new AFolder(fileOrFolderNameWithoutFullPath, backupFolderName);

    		/**
    		 * FOLLOWING CODE REPEATS IN BACKUP.JAVA, If you're changing anything here, check if the same change is necessary in BACKUP.JAVA
    		 */
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(folderOrStringPath)) {
            	
            	directoryLoop: for (Path path : directoryStream) {
                	   
                	//  if it's a file, check if it's in the excludefilelist
                	if (!(Files.isDirectory(path))) {
                		
                		// check if the file is in the list of files to exclude, example .DS_Store
                		if (excludefilelist.contains(path.getFileName().toString())) {
                    		continue;
                		}
                		
                		// check if the file is of format .849C9593-D756-4E56-8D6E-42412F2A707B seems a Microsoft hidden file
                		if (OtherUtilities.fileNeedsToBeIgnored(path.getFileName().toString())) {
                			continue;
                		}
                		
                	} else {
                		// check if folder is in excludedPaths
                		for (String excludedPath : excludedpathlist) {
                			
                			if (path.getFileName().toString().trim().equals(excludedPath.trim())) {
                				processText.process("      Excluding folder '" + excludedPath + "' because " + excludedPath + " is in the file excludedpathlist");
                				continue directoryLoop;
                			}
                		}
                		
                	}
                	
                	returnValue.addFileOrFolder(createAFileOrAFolder(path, backupFolderName, excludefilelist, excludedpathlist, addpathlength, processText));
                		
                }
                
            }
            
            if (addpathlength) {
                System.out.println("path length = " + String.format("%5s", folderOrStringPath.toString().length()) + "; path = " + folderOrStringPath.toString());
            }
            
            return returnValue;
            
    	} else {
    		
    		return new AFile(fileOrFolderNameWithoutFullPath, Files.getLastModifiedTime(folderOrStringPath).toMillis(), backupFolderName); 

    	}
    
    }
    
    	/**
    	 * compares source and dest which are both intance of AFileOrAFolder<br>
    	 * Updates dest:<br>
    	 * - If there's a folder in source, that is not in dest, then the folder must be added in dest<br>
    	 * - If there's a folder in dest, that is not in source, then the folder must be deleted in dest<br>
    	 * - If a file in source has a more recent last modified timestamp than the same file in dest, then the entry in dest must be updated with the new timestamp<br>
    	 *    - in that case the actual file is also copied to the destination folder
    	 * - If there's a file in source, that is not in dest, then the file must be added in dest<br>
    	 *    - in that case the actual file is also copied to the destination folder
    	 * - If there's a file in dest that is not found in source, then the entry in dest must be deleted<br>
    	 * @param sourceFolderPath the source root path where the actual source files are stored
    	 * @param destBackupFolderPath the source backup folder path where files need to be copied to
    	 * @param sourceFileOrFolder instance of AFileOrAFolder that represents the contents in sourceFolderPath
    	 * @param subfolders is an arraylist of strings, representing the subfolders. We need to pass them through as we go recursively through the function. It's needed in case a file copy needs to be made to make sure we put it in the right folder.
    	 * @param level first folder is treated a bit different when it comes to deletion, that's why this level is used
    	 * @param backupFolderName needed to write the pathToBackup
    	 * @param commandLineArguments the commandlinearguments
    	 */
        public static void compareAndUpdate(AFileOrAFolder sourceFileOrFolder, AFileOrAFolder destFileOrFolder, Path sourceFolderPath, Path destBackupFolderPath, ArrayList<String> subfolders, String backupFolderName, Integer level, CommandLineArguments commandLineArguments) {
        	
            // Compare and update files and folders
            if (sourceFileOrFolder instanceof AFile  && destFileOrFolder instanceof AFile) {
                // Compare and update files
                compareAndUpdateFiles((AFile) sourceFileOrFolder, (AFile) destFileOrFolder, sourceFolderPath, destBackupFolderPath, subfolders, backupFolderName, commandLineArguments);
            } else if (!(sourceFileOrFolder instanceof AFile) && !(destFileOrFolder instanceof AFile)) {
                // Compare and update folders
                compareAndUpdateFolders((AFolder) sourceFileOrFolder, (AFolder) destFileOrFolder, sourceFolderPath, destBackupFolderPath, OtherUtilities.addString(subfolders, sourceFileOrFolder.getName()), backupFolderName, level, commandLineArguments);
            } else {
            	commandLineArguments.processText.process("In compareAndUpdate(AFileOrAFolder source, AFileOrAFolder dest), not both File and not both Folder");
            	commandLineArguments.processText.process("   this is a difficult situation. It looks like an item that was previously a file is now a folder with the same name, or vice versa.");
            	commandLineArguments.processText.process("   Backup interrupted");
            	Thread.currentThread().interrupt();return;
            }
        }

        /**
         * 
         * @param folderlistPath Path for the folderlist.json
         * @return
         */
        public static AFileOrAFolder fromFolderlistDotJsonToAFileOrAFolder(Path folderlistPath, ProcessText processText) {
        	
            // declare and init listOfFilesAndFoldersInPreviousBackupFolder
            // it's null, but we assume that it will be set to non nul value, or an exception will occur causing a crash
            AFileOrAFolder listOfFilesAndFoldersInPreviousBackupFolder = null;
            
            try {

                ObjectMapper objectMapper = new ObjectMapper();
                listOfFilesAndFoldersInPreviousBackupFolder = objectMapper.readValue(Files.readString(folderlistPath, StandardCharsets.UTF_8), AFileOrAFolder.class);
                
            } catch (IOException e) {
                // Handle IOException (e.g., file not found or permission issues)
            	e.printStackTrace();
            	processText.process("Exception while converting file " + folderlistPath.toString() + " to json");
                processText.process(e.toString());
                Thread.currentThread().interrupt();
            }
            
            return listOfFilesAndFoldersInPreviousBackupFolder;
            

        }
        
        /**
         * search in destContents for sourceItem
         * @param sourceItem
         * @param destContents
         * @return
         */
        public static AFileOrAFolder findMatchingItem(AFileOrAFolder sourceItem, List<AFileOrAFolder> destContents) {
            // Find an item in dest with the same name as the sourceItem
            return destContents.stream()
                    .filter(destItem -> destItem.getName().equals(sourceItem.getName()))
                    .findFirst()
                    .orElse(null);
        }

        /**
         * this function creates an instance of AFileOrAFolderForFullPath, starting from an instance of AFileOrAFolder<br>
         * It's called recursively, that's why it has also as arguments an array of subfolders and the parentFolder which is an instance of AFolderWithFullPath<br>
         * AFileOrAFolderForFullPath contains the same information as AFileOrAFolder but a bit changed:<br>
         * - the path attribute in a folder contains the full path (inclusive all subfolders) if it also has files<br>
         * - ts is now a human readable timestamp, local time<br>
         * - pathToBackup is only filled in only for files not for folders<br>
         * <br>
         * Goal of all this is to have an easily searchable json file. Searchable for files, and to have an easy why of the full path of the containing folder and also the last modified timestamp<br>
         * <br>
         * The size of the json file (which is created later) must be less than 50 MB, which allows to open the file with the free version of Dadroid json viewer<br>
         * @param aFileOrAFolder
         * @param subfolders
         * @param parentFolder
         * @return
         */
        public static AFileOrAFolderForFullPath createAFileOrAFolderWithFullPath(AFileOrAFolder aFileOrAFolder, ArrayList<String> subfolders, AFolderWithFullPath parentFolder) {
        	
        	if (aFileOrAFolder instanceof AFile) {
        		
        		AFile aFileOrAFolderAsFile = (AFile)aFileOrAFolder;
        		
        		AFileWithLastModified returnValueAFile = new AFileWithLastModified(aFileOrAFolder.getName(), aFileOrAFolderAsFile.getPathToBackup());
        		returnValueAFile.setts(OtherUtilities.dateToString(new Date(aFileOrAFolderAsFile.getts()), Constants.OUTPUTDATEFORMAT_STRING));
        		
        		if (parentFolder != null) {

        			parentFolder.setPath(OtherUtilities.concatenateStrings(subfolders));
        			
        		}
        		
        		return returnValueAFile;   
        		
        	} else {
        		
        		AFolder afileAFolderAsFolder = (AFolder)aFileOrAFolder;
        		
        		// for folders we don't need the backup folder, just the subfoldername
        		AFolderWithFullPath returnValueAFolder = new AFolderWithFullPath(aFileOrAFolder.getName(), "");
        		
        		
        		for (AFileOrAFolder aFileOrAFolder1: ((AFolder)aFileOrAFolder).getFileOrFolderList()) {
        			
        			returnValueAFolder.getFileOrFolderList().add(createAFileOrAFolderWithFullPath(aFileOrAFolder1, OtherUtilities.addString(subfolders, afileAFolderAsFolder.getName()), returnValueAFolder));
        			
        		}
        		
        		return returnValueAFolder;
        		
        	}
        	
        }
        
        private static void compareAndUpdateFiles(AFile sourceFile, AFile destFile, Path sourceFolderPath, Path destBackupFolderPath, ArrayList<String> subfolders, String backupFolderName, CommandLineArguments commandLineArguments) {
            // Compare and update files based on last modified timestamp
            if (sourceFile.getts() != destFile.getts()) {
            	
            	// create logtext depending if getts > or < 
            	String additionalLogTextString = "";
            	if (sourceFile.getts() < destFile.getts()) {
            		additionalLogTextString = " - this file has an older date in the source, looks like an older/restored version was stored.";
            	}
            	
                // Update destFile with the new timestamp
                destFile.setts(sourceFile.getts());
                commandLineArguments.processText.process("   Copying updated file " + OtherUtilities.concatenateStrings(OtherUtilities.addString(subfolders, sourceFile.getName())) + additionalLogTextString);
                
                // set also the backup foldername
                destFile.setPathToBackup(backupFolderName);
                
                // create the folder in the destination if it doesn't exist yet
                try {
					Files.createDirectories(PathUtilities.concatenatePaths(destBackupFolderPath, subfolders));
				} catch (IOException e) {
					e.printStackTrace();
					commandLineArguments.processText.process("Exception in compareAndUpdateFiles(AFile,AFile) while creating the directory " + PathUtilities.concatenatePaths(destBackupFolderPath, subfolders).toString());
					commandLineArguments.processText.process(e.toString());
		            Thread.currentThread().interrupt();return;
				}
                
                try {
                	// add sourcefile name to dest and source file, it's the same name
                	Path destPath = PathUtilities.concatenatePaths(destBackupFolderPath, OtherUtilities.addString(subfolders, sourceFile.getName()));
					Files.copy(PathUtilities.concatenatePaths(sourceFolderPath, OtherUtilities.addString(subfolders, sourceFile.getName())), destPath, StandardCopyOption.COPY_ATTRIBUTES);
	                if (commandLineArguments.addpathlengthforfolderswithnewormodifiedcontent) {
	                    System.out.println("path length = " + String.format("%5s", destPath.toString().length()) + "; path = " + destPath.toString());
	                }

				} catch (IOException e) {
					e.printStackTrace();
					commandLineArguments.processText.process("Exception in compareAndUpdateFiles(AFile,AFile) while copying a file from " + PathUtilities.concatenatePaths(sourceFolderPath, subfolders).toString() + " to " + PathUtilities.concatenatePaths(destBackupFolderPath, subfolders));
					commandLineArguments.processText.process(e.toString());
		            Thread.currentThread().interrupt();return;
				}
                
            } 
        }

        private static void compareAndUpdateFolders(AFolder sourceFolder, AFolder destFolder, Path sourceFolderPath, Path destBackupFolderPath, ArrayList<String> subfolders, String backupFolderName, Integer level, CommandLineArguments commandLineArguments) {
            // Compare and update folders based on content
            List<AFileOrAFolder> sourceContents = sourceFolder.getFileOrFolderList();
            List<AFileOrAFolder> destContents = destFolder.getFileOrFolderList();

            // Process files and folders in source
            for (AFileOrAFolder sourceItem : sourceContents) {
            	
            	// for the foldername mapping, we need to compare to the mapped name, so if a mapping is found, then we store the original name
            	String originalSourceItemName = sourceItem.getName();
            	String newSourceItemName = originalSourceItemName;
            	
            	// If it's a folder, and if it's the first level, then we'll do foldernamemapping
            	if (
            			   (level == 1) 
            			&& (sourceItem instanceof AFolder) 
            			&& (commandLineArguments.folderNameMapping.containsKey((originalSourceItemName)))
            			&& (commandLineArguments.folderNameMapping.get(originalSourceItemName) != null)
            		) {
            		newSourceItemName = commandLineArguments.folderNameMapping.get(originalSourceItemName);
            		sourceItem.setName(newSourceItemName);
            	}
            	
                // Find the corresponding item in dest
                AFileOrAFolder matchingDestItem = findMatchingItem(sourceItem, destContents);

                // set back the original source item name, we will replace again later on by calling the funcion doFolderNameMapping, somewhere else
            	sourceItem.setName(originalSourceItemName);

                if (matchingDestItem == null) {
                	
                    destContents.add(sourceItem);
                	if (sourceItem instanceof AFile) {

                		commandLineArguments.processText.process("   Adding new file : " + OtherUtilities.concatenateStrings(OtherUtilities.addString(subfolders, originalSourceItemName)));
                    	
                        // create the folder in the destination if it doesn't exist yet
                        try {
        					Files.createDirectories(PathUtilities.concatenatePaths(destBackupFolderPath, subfolders));
        				} catch (IOException e) {
        					e.printStackTrace();
        					commandLineArguments.processText.process("Exception in compareAndUpdateFiles(AFileOrAFolder, AFileOrAFolder.. while creating the directory " + PathUtilities.concatenatePaths(destBackupFolderPath, subfolders).toString());
        					commandLineArguments.processText.process(e.toString());
        		            Thread.currentThread().interrupt();return;
        				}
                        
                        try {
                        	// add sourcefile name to dest and source file, it's the same name
                        	Path destPath = PathUtilities.concatenatePaths(destBackupFolderPath, OtherUtilities.addString(subfolders, originalSourceItemName));
        					Files.copy(PathUtilities.concatenatePaths(sourceFolderPath, OtherUtilities.addString(subfolders, originalSourceItemName)), destPath, StandardCopyOption.COPY_ATTRIBUTES);
        	                if (commandLineArguments.addpathlengthforfolderswithnewormodifiedcontent) {
        	                    System.out.println("path length = " + String.format("%5s", destPath.toString().length()) + "; path = " + destPath.toString());
        	                }


        				} catch (IOException e) {
        					e.printStackTrace();
        					commandLineArguments.processText.process("Exception in compareAndUpdateFiles(AFileOrAFolder, AFileOrAFolder.. while copying a file from " + PathUtilities.concatenatePaths(sourceFolderPath, subfolders).toString() + " to " + PathUtilities.concatenatePaths(destBackupFolderPath, subfolders));
        					commandLineArguments.processText.process(e.toString());
        		            Thread.currentThread().interrupt();return;
        				}


                	} else if (sourceItem instanceof AFolder) {// it has to be an instance of AFolder but let's check anyway
                		
                		commandLineArguments.processText.process("   Adding new folder and it's contents : " + OtherUtilities.concatenateStrings(OtherUtilities.addString(subfolders, originalSourceItemName)));

                		// we need to copy the complete contents of the folder from source to dest
                		try {
							OtherUtilities.copyFolder(PathUtilities.concatenatePaths(sourceFolderPath, OtherUtilities.addString(subfolders, originalSourceItemName)), PathUtilities.concatenatePaths(destBackupFolderPath, OtherUtilities.addString(subfolders, originalSourceItemName)), commandLineArguments, commandLineArguments.processText);
						} catch (IOException e) {
							e.printStackTrace();
							commandLineArguments.processText.process("Exception in compareAndUpdateFiles(AFileOrAFolder, AFileOrAFolder.. while copying a folder from " + PathUtilities.concatenatePaths(sourceFolderPath, subfolders).toString() + " to " + PathUtilities.concatenatePaths(destBackupFolderPath, subfolders));
							commandLineArguments.processText.process(e.toString());
        		            Thread.currentThread().interrupt();return;
						}
                		
                	}
                	
                	
                } else {
                	
                    // Recursively compare and update the matching items
                    compareAndUpdate(sourceItem, matchingDestItem, sourceFolderPath, destBackupFolderPath, subfolders, backupFolderName, level + 1, commandLineArguments);
                    
                    // before leaving the function set matchingDestItem name to the originalSourceItemName
                    // later on we will call doFolderNameMapping, which iterates through the destination folder list. It will see that there's a mapping to be applied and then also rename the backuped folder
                    matchingDestItem.setName(originalSourceItemName);
                    
                }
            }

            // Process items in dest that don't exist in source
            // but only for not level 1 folders, meaning once a backup is taken of a sharepoint library, it will not be removed anymore in the backup
            if (level > 1) {
            	
            	int cntr = 0;
            	while (cntr < destContents.size()) {
            		
            		AFileOrAFolder destItem = destContents.get(cntr);
            		
            		// Find the corresponding item in sourceContents
                    AFileOrAFolder matchingSourceItem = findMatchingItem(destItem, sourceContents);
                    
                    if (matchingSourceItem == null) {
                    	// there's no item in source with the same name, so we can remove it from dest
                    	destContents.remove(cntr);
                    	String fullPathString = OtherUtilities.concatenateStrings(OtherUtilities.addString(subfolders, destItem.getName()));
                    	if (destItem instanceof AFolder) {
                    		commandLineArguments.processText.process("   Removed folder " + fullPathString);
                    	} else {
                    		commandLineArguments.processText.process("   Removed file " + fullPathString);
                    	}
                    } else {
                    	cntr = cntr +1;
                    }
                    
            	}
            	
            }
        }
        
        /**
         * 
         * @param backupFolder example '2024-01-12 16;46;55 (Full)'
         * @param folder example XDrive/Werking/subfolder
         * @param commandLineArguments
         * @return true if folderlist.json in backup folder contains XDrive/Werking/subfolder
         */
        public static boolean folderExistsInBackup(String backupFolder, String folder, String backupDestination, ProcessText processText) {
        	
        	Path pathWithJsonFile = Paths.get(backupDestination).resolve(backupFolder).resolve("folderlist.json");
			
        	AFileOrAFolder listOfFilesAndFoldersInBackupFolder = FileAndFolderUtilities.fromFolderlistDotJsonToAFileOrAFolder(pathWithJsonFile, processText);
			
        	if (!(listOfFilesAndFoldersInBackupFolder instanceof AFolder)) {
        		// this would mean the backupFolder is not a folder which looks like a coding error, return false in that case
        		return false;
        	}
        	
        	// now check if listOfFilesAndFoldersInBackupFolder contains XDrive/Werking/subfolder
        	
        	// first split folder by seperator
        	Path[] subfolders = PathUtilities.splitPath(Paths.get(folder));
        	
        	int subfoldersCounter = 0;
        	
        	boolean foundFolderInBackupFolder = true;
        	
        	while (subfoldersCounter < subfolders.length) {
        		// if subfolder is an empty string, then actually no subfolder is specified
        		// in this case, the loop exist immediately
        		if (subfolders[subfoldersCounter].toString().length() == 0) {
        			break;
        		}
        		
        		AFileOrAFolder folderFound = FileAndFolderUtilities.findMatchingItem(new AFolder(subfolders[subfoldersCounter].toString(), ""), ((AFolder)listOfFilesAndFoldersInBackupFolder).getFileOrFolderList());
        		
        		if (folderFound == null) {
        			// element not found
        			return false;
        		}
        		
        		if (!(folderFound instanceof AFolder)) {
            		// this would mean we found a matching item, but it's not a folder, which is also an abnormal case
            		return false;
            	}
        		
        		listOfFilesAndFoldersInBackupFolder = folderFound;
        		subfoldersCounter++;
            	
        	}
        	
        	return foundFolderInBackupFolder;
        	
        }

}
