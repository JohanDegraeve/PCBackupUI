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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.AFile;
import model.AFileOrAFolder;
import model.AFolder;
import model.CommandLineArguments;
import model.Constants;
import utilities.FileAndFolderUtilities;
import utilities.ListBackupsInFolder;
import utilities.Logger;
import utilities.OtherUtilities;
import utilities.WriteToFile;

public class Search {


	public static void search(CommandLineArguments commandLineArguments) {
	
		/**
		 * where to find the backup files, this is the backup folder path without the specific folder (ie without '2023-12-06 18;24;41 (Full)' or anything like that)
		 */
        Path sourceFolderPath = Paths.get(commandLineArguments.destination);
        
		// get list of all backup folders at and before endSearchDate
    	// first get the latest, then get all older ones and insert the latest
    	// this is trick to reuse existing functions
        List<String> allBackups = new ArrayList<>();
        try {
			String latestBackupFolderName = ListBackupsInFolder.getMostRecentBackup(sourceFolderPath, commandLineArguments.endSearchDate);
			allBackups = ListBackupsInFolder.getAllBackupFoldersAsStrings(sourceFolderPath, latestBackupFolderName);
			allBackups.add(0, latestBackupFolderName);
        } catch (IOException e) {
			e.printStackTrace();
			Logger.log("Exception in search, while reading backup folder ");
            Logger.log(e.toString());
        }
        
        // will have search results after calling iterateThroughFolderOrFile 
		Map<String, String> results = new HashMap<>();
		
		// iterate through all backups older than startSearchDate and search for the text
		for (String backupFolderName: allBackups) {
			
			if (OtherUtilities.getBackupDate(backupFolderName).compareTo(commandLineArguments.startSearchDate) < 0) {
				continue;
			}
			
			Path pathWithJsonFile = sourceFolderPath.resolve(backupFolderName).resolve("folderlist.json");
			Logger.log("Parsing " + pathWithJsonFile.toString());
			AFileOrAFolder listOfFilesAndFoldersInBackup = FileAndFolderUtilities.fromFolderlistDotJsonToAFileOrAFolder(pathWithJsonFile);
			
			iterateThroughFolderOrFile(listOfFilesAndFoldersInBackup, commandLineArguments.searchTextPattern, backupFolderName, Paths.get(""), results);
			
		}

		// seperator to use in csv files
		String seperator = ",";
		
		// string where search results will be written to and finally written to output file
		String textToWrite = "";

		// first add "sep=," this tells Excel that , is the seperator. ";" can't be the seperator because ";" is already used in the backup folder names
		textToWrite += "sep=,\n";

		// we have the results, create text to write to file
		textToWrite += "name of matching item" + seperator + "type" + seperator + "last modified" + seperator + "backupfolder where file was found for the last time" + seperator + "path to backup folder where latest version is stored" + seperator + "folder name within backup\n";
		for (Map.Entry<String, String> entry : results.entrySet()) {
            
			// first determine if it's a file or a folder by checking and removing the last part of the key
			// key ends with either -AFOLDER or -AFILE
			// and get the real path
			boolean itsafile = true;
			String thePath = "";
			if (entry.getKey().endsWith( "-AFOLDER")) {
				itsafile = false;
				thePath = entry.getKey().substring(0, entry.getKey().length() - 8);
			} else {
				thePath = entry.getKey().substring(0, entry.getKey().length() - 6);
			}

			// get the value , example value '2024-03-10 17;53;19 (Incremental)|||2024-03-23 12;14;01 (Incremental)|||2024 02 10 00:07:19'
			// in this example:
			// - lastmodifiedtimestamp = 2024 02 10 00:07:19
			// - lastBackupFolderName = 2024-03-23 12;14;01 (Incremental)
			// - backupFolderWithLatestVersionOfTheFileOrFolder = 2024-03-10 17;53;19 (Incremental)
			String value = entry.getValue();
			String lastBackupFolderName_Plus_lastmodifiedtimestamp = value.substring(value.indexOf("|||") + 3, value.length());
			String lastmodifiedtimestamp = lastBackupFolderName_Plus_lastmodifiedtimestamp.substring(lastBackupFolderName_Plus_lastmodifiedtimestamp.indexOf("|||") + 3, lastBackupFolderName_Plus_lastmodifiedtimestamp.length());
			String lastBackupFolderName = lastBackupFolderName_Plus_lastmodifiedtimestamp.substring(0, lastBackupFolderName_Plus_lastmodifiedtimestamp.length() - lastmodifiedtimestamp.length() - 3);
			
			// and now the path
			String backupFolderWithLatestVersionOfTheFileOrFolder = value.substring(0, value.length() - lastBackupFolderName_Plus_lastmodifiedtimestamp.length() - 3);
			
			// full backupfolder example /Users/johandegraeve/Downloads/dest/2024-03-10 17;53;19 (Incremental)
			Path backupFolder = sourceFolderPath.resolve(backupFolderWithLatestVersionOfTheFileOrFolder);
			
			// example submap1/submap12/submap121/Jabra Speak 510 user manual_EN_English_RevL.pdf
			Path subFolderWithItem = Paths.get(thePath);
			
			// FIRST COLUMN = filename (if it's a file) or subfoldername (if it's a folder)
			// write name of matching item, this is just the last part of subFolderWithItem
			textToWrite += subFolderWithItem.getFileName().toString()  + seperator;
			
			// SECOND COLUMN type, 'file' or 'folder'
			// write if it's a file or a folder
			textToWrite += (itsafile ? "file":"folder") + seperator; 
			
			// THIRD COLUMN, if a file, the last modified timestamp
			textToWrite += lastmodifiedtimestamp + seperator;
			
			// FOURTH COLUMN - backupfolder where file or folder was found for the last time
			// add lastBackupFolderNameString
			textToWrite += lastBackupFolderName + seperator;
			
			// FIFTH COLUMN - path to backup folder where latest version is stored
			// add path to backup folder
			textToWrite += backupFolder.toString()  + seperator;
			
			// SIXTH COLUMN - folder name within backup
			// now add the full path, if it's a file, then just the folder, not the filename
			if (itsafile) {
				if (subFolderWithItem.getParent() != null) {
					textToWrite += subFolderWithItem.getParent().toString();
				} // else we write nothing because that would mean it's a file in the root folder, getparent is null, so we just don't write anything
			} else {
				textToWrite += subFolderWithItem.toString();
			}
			
			
			// add a newline
			textToWrite += "\n";
			
        }
		
		// get path filename to write to
		Path pathToWriteTo = Paths.get(commandLineArguments.writesearchto).resolve(createSearchResultFilename(Paths.get(commandLineArguments.writesearchto)));
		
		try {
			WriteToFile.writeToFile(textToWrite, pathToWriteTo.toString());
			Logger.log("Search results written to " + pathToWriteTo.toString());
		} catch (IOException e) {
			e.printStackTrace();
        	Logger.log("Failed to write search results to  " + pathToWriteTo.toString());
			System.exit(1);
		}

	}
	
	/**
	 * Go through all subfolders and files in aFileOrAFolder, search the item (subfolder or file) with matching name, if found, get the backup folder name<br>
	 * updates results with new results<br>
	 * Only adds matching items that are not yet in results, because some items may have been found in more recent backups. Goal is that the function is first called for the most recent backup<br>
	 * <br>
	 * Key also has an indication if it's a file or a folder, by adding either "-AFILE" or "-AFOLDER"<br>
	 * <br>
	 * Value also contains the backup folder name where we found it for the last time (which is not the same as the backup folder where the last version of the file is stored - seperated by |||<br>
	 * Value also has the last modified timestamp, also seperated from the rest by |||
	 * <br>
	 * example: (here searchTextPattern could be "test"<br>
	 *   - key = submap2/submap21/submap211/test.txt-AFILE => it's a file with name test.txt in folder submap2/submap21/submap211<br>
	 *   - value = 2024-03-10 17;53;19 (Incremental)|||2024-03-15 12;53;19 (Incremental)|||2024 02 12 21:35:40<br>
	 *   &nbsp&nbsp&nbsp - => backup where we find the latest version = 2024-03-10 17;53;19 (Incremental)<br>
	 *   &nbsp&nbsp&nbsp - => last backup folder where the file was found = 2024-03-15 12;53;19 (Incremental)<br>
	 *   &nbsp&nbsp&nbsp - => if it's a file last modified itmestamp of the file = 2024 02 12 21:35:40<br>
	 * <br>
	 * @param aFileOrAFolder instance of AFileOrAFolder to search in. This should always match the contents of sourceFolderPath/backupfoldername/subfolder, meaning the caller must make sure this is a correct match
	 * @param searchTextPattern to search, regex is used
	 * @param subfolder is subfolder within a backup where the actual aFileOrAFolder is stored, this value is used to create the key value. This is a full path example submap2/submap21/submap211
	 * @param results instance of Map<String, String> which contains the results after calling the function
	 */
	private static void iterateThroughFolderOrFile(AFileOrAFolder aFileOrAFolder, Pattern searchTextPattern, String backupfoldername, Path subfolder, Map<String, String> results) {
		
		Matcher matcher = searchTextPattern.matcher(aFileOrAFolder.getName());
		
		if (matcher.find()) {
			
			// path without backup folder name
			Path pathWhereItemWasFound = subfolder;
			if (aFileOrAFolder instanceof AFile) {
				pathWhereItemWasFound = pathWhereItemWasFound.resolve(aFileOrAFolder.getName());
			}
			
			// if it's a file, then we add "-AFILE" at the end, otherwise "-AFOLDER"
			String finalKeyToAdd = pathWhereItemWasFound.toString();
			if (aFileOrAFolder instanceof AFile) {
				finalKeyToAdd += "-AFILE";
			} else {
				finalKeyToAdd += "-AFOLDER";
			}
			
			// to the value we will add at the end the backupfoldername, this will be cut off again before writing to csv
			// we add three pipe delimiters
			String valueToAdd = aFileOrAFolder.getPathToBackup() + "|||" + backupfoldername;
			
			// also add the last modified timestamp if it's a file
			valueToAdd += "|||" ;
			if (aFileOrAFolder instanceof AFile) {
				valueToAdd += OtherUtilities.dateToString(new Date(((AFile)aFileOrAFolder).getts()), Constants.OUTPUTDATEFORMAT_STRING);
			}
			
			// if not yet in results, then add it, with the backup folername where the latest version is stored
			if (!results.containsKey(finalKeyToAdd)) {
				results.put(finalKeyToAdd, valueToAdd);
			}
			
			
		}
		
		if (aFileOrAFolder instanceof AFile) {
			// no further processing needed
		} else {
			for (AFileOrAFolder aFileOrAFolder1: ((AFolder)aFileOrAFolder).getFileOrFolderList()) {
				
				if (aFileOrAFolder1 instanceof AFile) {

					iterateThroughFolderOrFile(aFileOrAFolder1, searchTextPattern, backupfoldername, subfolder, results);

				} else {

					iterateThroughFolderOrFile(aFileOrAFolder1, searchTextPattern, backupfoldername, subfolder.resolve(aFileOrAFolder1.getName()), results);

				}
				
			}
		}
		
	}
	
	/**
	 * will check if 'searchresults.txt' exists in path, if it exists it tries with 'searchresults (1).txt' , 'searchresults (2).txt' ... until a not yet existing file is found<br>
	 * if not existing filename is found, it is returned. Max up to 1000 attempts, if not found within 1000, it returns "searchresults.txt" 
	 * @param path must be an existing folder 
	 * @return the filename to use
	 */
	private static String createSearchResultFilename(Path path) {
		
		Path pathToCheck = path.resolve("searchresults.csv");
		
		// check if searchresults already exists, and if not,then this will be the file name
		if (!Files.exists(pathToCheck)) {
			return "searchresults.csv";
		}
		
		for(int i = 1; i <= 1000; i++) {
		    String newFileName = "searchresults (" + i + ").csv"; 
		    pathToCheck = path.resolve(newFileName);
		    if (!Files.exists(pathToCheck)) {
				return newFileName;
			}
		}
		
		return "searchresults.csv";
		
	}
	
}
