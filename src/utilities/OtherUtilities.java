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
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.AFileOrAFolder;
import model.AFolder;
import model.CommandLineArguments;
import model.Constants;

public class OtherUtilities {

	public static ArrayList<String> addString(ArrayList<String> source, String stringToAdd) {
		
		ArrayList<String> copyList = new ArrayList<>(source);
        copyList.add(stringToAdd);

		return copyList;
		
	}
	
	
	public static boolean fileNeedsToBeIgnored(String fileName) {
		
		if (fileIsWindowsHiddenFile(fileName)) {return true;}
		
		return false;
		
	}
	
	/**
	 * concatenates subfolders with a / or \ between the names, depending on the platform
	 * @param source array of subfolders, eg submap1, submap2
	 * @return in example submap1/submap2, seperator is platform dependent
	 */
	public static String concatenateStrings(ArrayList<String> source) {
		String returnValue = "";
		for (String sourceItem : source) {
			String seperatorToAdd = "";
			if (returnValue.length() != 0) {
				seperatorToAdd = File.separator;
			}
			returnValue = returnValue +seperatorToAdd + sourceItem;
		}
		return returnValue;
	}
	
	/**
	 * example files of format .849C9593-D756-4E56-8D6E-42412F2A707B need to be ignored
	 * @param fileName
	 * @return
	 */
	private static boolean fileIsWindowsHiddenFile(String fileName) {
		
		if (!(fileName.startsWith("."))) {return false;}
			
		if (!(fileName.length() == 37)) {return false;}	
		
		// Define the regex pattern
        String pattern = "^\\.[A-F0-9]{8}-[A-F0-9]{4}-[A-F0-9]{4}-[A-F0-9]{4}-[A-F0-9]{12}$";

        // Create a Pattern object
        Pattern regex = Pattern.compile(pattern);

        // Create a Matcher object
        Matcher matcher = regex.matcher(fileName);

        // Check if the filename matches the pattern
        return matcher.matches();
		
	}
	
	/**
	 * recursively copying a folder from a source directory to a destination directory
	 * @param source
	 * @param destination
	 * @param commandLineArguments
	 * @throws IOException
	 */
	public static void copyFolder(Path source, Path destination, CommandLineArguments commandLineArguments) throws IOException {
        Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                // Create corresponding directory in the destination
                Path targetDir = destination.resolve(source.relativize(dir));
                Files.createDirectories(targetDir);
                if (commandLineArguments.addpathlengthforfolderswithnewormodifiedcontent) {
                    System.out.println("path length = " + String.format("%5s", targetDir.toString().length()) + "; path = " + targetDir.toString());
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            	
            	Path destinationPath = destination.resolve(source.relativize(file));
            	
        		// check if the file is in the list of files to exclude, example .DS_Store
        		if (commandLineArguments.excludedFiles.contains(destinationPath.getFileName().toString())) {
        			return FileVisitResult.CONTINUE;
        		}
        		
        		// check if the file is of format .849C9593-D756-4E56-8D6E-42412F2A707B seems a Microsoft hidden file
        		if (OtherUtilities.fileNeedsToBeIgnored(destinationPath.getFileName().toString())) {
        			return FileVisitResult.CONTINUE;
        		}
            	
                // Copy each file to the destination
                Files.copy(file, destinationPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
                if (commandLineArguments.addpathlengthforfolderswithnewormodifiedcontent) {
                    System.out.println("path length = " + String.format("%5s", destinationPath.toString().length()) + "; path = " + destinationPath.toString());
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }
	
	/**
	 * does the foldername mapping for the first folder, explanation see in commandline arguments, with the explanation of argument folderNameMapping
	 * @param listOfFilesAndFoldersInSourceFolder
	 * @param commandLineArguments
	 * @param sourceFolderPath
	 * @param destinationFolderPath must include the backup foldername
	 */
	public static void doFolderNameMapping(AFolder listOfFilesAndFoldersInSourceFolder, CommandLineArguments commandLineArguments, Path destinationFolderPath) {
		// now we will do foldername mapping
		// if one of the main folders in the source is fond in the foldername mapping list, then we update it in folderlist.json and we also rename the actual foldername in the backup
		for (AFileOrAFolder aFileOrAFolder: listOfFilesAndFoldersInSourceFolder.getFileOrFolderList()) {
			
			// we only do the mapping for folders
			if (!(aFileOrAFolder instanceof AFolder)) {continue;}
			
			if (commandLineArguments.folderNameMapping.containsKey((aFileOrAFolder).getName())) {
				
				String newName = commandLineArguments.folderNameMapping.get((aFileOrAFolder).getName());
				
				String oldName = aFileOrAFolder.getName();
				
				if (newName != null) {
					
					aFileOrAFolder.setName(newName);
					
					//now rename the actual backup folder
					Path sourcePath = destinationFolderPath.resolve(oldName);
					Path targetPath = destinationFolderPath.resolve(newName);
					
					// possibly the sourcepath doesn't exist, eg if it's an incremental backup, and there was no modified file in the folder and it's subfolders
					// then the path is not created, so Files.move would file
					// so first check if it exists.
					
					try {
						if (Files.exists(sourcePath)) {
							Files.move(sourcePath, targetPath);
						}
					} catch (IOException e) {
						e.printStackTrace();
						Logger.log("Exception occurred while renaming from " + sourcePath.toString() + " to " + targetPath.toString());
						System.exit(1);

					}
				}
				
			}
			
		}

	}
	
	/**
	 * converts date to locale String
	 * @param date
	 * @return
	 */
	public static String dateToString(Date date, String dateFormat) {
		
        // Convert Date to Instant
        Instant instant = date.toInstant();

        // Create a ZonedDateTime using the default time zone
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());

        // Define a format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);

        // Format the ZonedDateTime
        return zonedDateTime.format(formatter);

	}

	/**
	 * 
	 * @param backupName eg backupName = 2024-03-19 23;06;08 (Incremental)
	 * @return date, in this example 2024-03-19 23;06;08 local time in Date object
	 */
	public static Date getBackupDate(String backupName) {
		
		if (backupName == null) {return new Date(0);}
		
		SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.BACKUPFOLDERDATEFORMAT_STRING);
		
		String dateAsString = backupName.substring(0,18);
		
		try {
			return dateFormat.parse(dateAsString);
		} catch (ParseException e) {
			e.printStackTrace();
			Logger.log("");
			System.exit(1);
		}
		
		return new Date(0);
		
	}
	
	/**
	 * removes double backslashes<br>
	 * needed for strings generated by jackson faster xml objectmapper.writeValueAsString<br>
	 * If a string in a class contains a backslash, this is stored internally with two slashes. Then running jackson objectmapper.writeValueAsString on it generates those two backslashes in the output<br>
	 * This function gets rid of those two backslashes.
	 * @param input is the result after calling objectmapper.writeValueAsString which contains attributes with backslashes
	 * @return
	 */
	public static String removeDoubleBackSlashes(String input) {
		
		// convert input to bytearray
		byte[] inputAsByteArray = input.getBytes();
		
		// create new bytearray with same length as inputAsByteArray, with 0 values
		byte[] newbytearray = new byte[inputAsByteArray.length];
		
		// we'll go through inputAsByteArray byte per byte. If we encounter the ASCII code for \ (92) then check if the next byte is also 92 and if yes, skip it
		
		// index in newbytearray
		int j = 0;
		
		for (int i = 0; i < inputAsByteArray.length;i++) {
			
			// copy byte from newbytearray to baosasbytearray
			newbytearray[j] = inputAsByteArray[i];
			
			// if two consecutive backslashes, then we will not increase j, which means the second instance of the backslash will be copied on the same index as the first
			if ((inputAsByteArray[i] == 92) && (inputAsByteArray[i + 1] == 92)) {// 92 is the ascii code for backslash
				// two consecutive backslashes
				// continue without increasing j, that means the second backslash will not be copied
			} else {
				j = j + 1;
				// i is increased automatically by the for loop
			}
		}
		
   		// Put things back
		return new String(newbytearray);
		
	}
	
	/**
	 * searches for value hashMap and if found returns the key<br>
	 * If not found, then returns value itself
	 * @param hashMap
	 * @param value
	 * @return
	 */
	public static String getKeyForValue(HashMap<String, String> hashMap, String value) {
		for (Map.Entry<String, String> entry : hashMap.entrySet()) {
            if (entry.getValue().equals(value)) {
                return  entry.getKey();
            }
        }
		return value;
	}

}
