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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import Interfaces.ProcessText;
import model.AFile;
import model.AFileOrAFolder;
import model.AFolder;
import model.UIParameters;
import utilities.FileAndFolderUtilities;
import utilities.ListBackupsInFolder;

public class CountDuplicates implements Runnable {

	ProcessText processText = null;
	
	public CountDuplicates(ProcessText processText) {
		this.processText = processText;
	}
	
	private Hashtable<String, Integer> occurrences = new Hashtable<>();
	
	/**
	 * 
	 */
	private ArrayList<ArrayList<String>> fullLists = new ArrayList<>();
	
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private void countDuplicates() {
		
		try {
			
			System.out.println("START");
			
			List<String> backupFoldersAsStrings = ListBackupsInFolder.getAllBackupFoldersAsStrings(Paths.get(UIParameters.getInstance().getDestTextFieldTextString()), "zzzz");
			String mostRecentBackup = backupFoldersAsStrings.get(0);
			
			Path destFolderPath = Paths.get(UIParameters.getInstance().getDestTextFieldTextString());
			
			// get the file folderlist.json as AFileOrAFolder
			Path pathWithJsonFile = destFolderPath.resolve(mostRecentBackup).resolve("folderlist.json");

			AFileOrAFolder listOfFilesAndFoldersInLastBackup = FileAndFolderUtilities.fromFolderlistDotJsonToAFileOrAFolder(pathWithJsonFile, null);
			
			findFirstFilename((AFolder)listOfFilesAndFoldersInLastBackup, (AFolder)listOfFilesAndFoldersInLastBackup, 0, "");
						
			for (String key : occurrences.keySet()) {
				
				String[] keySplitted = key.split("\\|\\|\\|");
				
				String fileTimeStamp = dateFormat.format(new Date(Long.parseLong(keySplitted[1])));
				
				if (processText != null) {
					processText.process(keySplitted[0] + ";" + fileTimeStamp + ";" + occurrences.get(key));
				}
	            //System.out.println(keySplitted[0] + ";" + fileTimeStamp + ";" + occurrences.get(key));
	            
	        }
			
			processText.process("=================================================");
			
			for (ArrayList<String> fullList: fullLists) {
				
				for (String entry: fullList) {
					
					if (processText != null) {
						
						String[] keySplitted = entry.split("\\|\\|\\|");
						
						String fileTimeStamp = dateFormat.format(new Date(Long.parseLong(keySplitted[1])));
						
						processText.process(fileTimeStamp + ";" + keySplitted[0]);
							
					}

		            //System.out.println(entry);
					
				}
				
			}
			
			System.out.println("STOP");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private int findFirstFilename(AFolder listOfFilesAndFolders, AFolder originalListOfFilesAndFolders, int counter, String path) {

		Iterator<AFileOrAFolder> iterator = listOfFilesAndFolders.getFileOrFolderList().iterator();
		
		while (iterator.hasNext()) {
			
			counter++;
			
			//System.out.println("In findFirstFilename counter = " + counter);
			
			AFileOrAFolder next = iterator.next();
			//System.out.println("next name = " + next.getName());
			
			
			if (next instanceof AFile) {
				
				AFile nextAsAFile = (AFile)next;
				
				if(nextAsAFile.getName().equalsIgnoreCase("commit.txt")) {
					//System.out.println("hier ben ik");
				}
				
				// if it's already in occurrences then skip it, it means it was already counted
				if (occurrences.get(createKey(nextAsAFile.getName(), nextAsAFile.getts())) == null) {
					
					// we need to have a new iterator pointing to iterator.next() but without using iterator
					
					// find duplicates
					//System.out.println("calling findDuplicates for "  + path + "\\" +  nextAsAFile.getName() + ", with skip = " + counter);
					ArrayList<String> newFullList = new ArrayList<>();
					findDuplicates(nextAsAFile.getName(), nextAsAFile.getts(), originalListOfFilesAndFolders.getFileOrFolderList().iterator(), counter, 0, "", newFullList);
					fullLists.add(newFullList);
					
				}
				
			} else {
				
				AFolder nextAsAFolder = (AFolder)next;
				
				
				counter = findFirstFilename(nextAsAFolder, originalListOfFilesAndFolders, counter, path + "\\" + nextAsAFolder.getName());
				
			}
			
		}
		
		return counter;
		
	}
	
	private int findDuplicates(String fileName, long timestamp, Iterator<AFileOrAFolder> iterator, int skip, int counter, String path, ArrayList<String> fullList) {
		
		int newCounter = counter;
		
		while (iterator.hasNext()) {
			
			newCounter++;
			
			/* System.out.println("In findDuplicatesen"
					+ " newCounter = " + newCounter);*/
			
			AFileOrAFolder next = iterator.next();
			
			// System.out.println("next = " + path + "\\" + next.getName());
			
			if (next instanceof AFile && newCounter > skip) {
				
				AFile nextAsAFile = (AFile)next;
				
				if(nextAsAFile.getName().equalsIgnoreCase("commit.txt")) {
					// System.out.println("hier ben ik");
				}
				
				if (nextAsAFile.getName().equals(fileName) && nextAsAFile.getts() == timestamp) {
					
					// System.out.println("found duplicate");
					
					// found a duplicate
					if (!occurrences.containsKey(createKey(fileName, timestamp))) {
						 
						occurrences.put(createKey(fileName, timestamp), 2);
						// System.out.println("new amount =  " + occurrences.get(createKey(fileName, timestamp)));
						
						// it's the first reoccurrence of a filename, here we create a full list array
						fullList.add(createKey(path + "\\" + fileName, timestamp));
						
					} else {
						
						occurrences.put(createKey(fileName, timestamp), occurrences.get(createKey(fileName, timestamp)) + 1);
						// System.out.println("new amount =  " + occurrences.get(createKey(fileName, timestamp)));
						fullList.add(createKey(path + "\\" + fileName, timestamp));
						
					}
					
				}
				
			} else {
				
				
				if (next instanceof AFolder) {
					// go through the folder with findDuplicates
					// System.out.println("calling findDuplicates for " + path + "\\" + next.getName() + ", with counter = " + newCounter);
					newCounter =  findDuplicates(fileName, timestamp, ((AFolder)next).getFileOrFolderList().iterator(), skip, newCounter, path + "\\" + next.getName(), fullList);
				}
				
			}

		}
		
		return newCounter;
		
	}
	
	private static String createKey(String fileName, long timestamp) {
		return fileName + "|||" + String.valueOf(timestamp);
	}
	
	@Override
	public void run() {
		countDuplicates();
	}
	
}
