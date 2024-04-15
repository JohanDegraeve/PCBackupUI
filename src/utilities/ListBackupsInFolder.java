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
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;

import model.Constants;

public class ListBackupsInFolder {

    /**
     * get the most recent backup folder, ie of format "yyyy-MM-dd HH;mm;ss (Incremental|Full)"
     * @param backupFolder root source folder where backups are stored
     * @return null if none found, this is just the subfolder name, not the full Path
     * @throws IOException
     */
    public static Path getMostRecentBackup(Path backupFolder) throws IOException {
        List<Path> backupFolders = getAllBackupFolders(backupFolder);
        if (backupFolders.size() == 0) {
        	return null;
        }
        return Collections.max(backupFolders);
    }

    /**
     * get the most recent backup folder, ie of format "yyyy-MM-dd HH;mm;ss (Incremental|Full)"
     * @param backupFolder root source folder where backups are stored
     * @param beforeDate we're searching for a backup made before this date or exactly the same date (either full or incremental)
     * @return null if none found, this is just the subfolder name, not the full Path
     * @throws IOException
     */
    public static String getMostRecentBackup(Path backupFolder, Date beforeDate) throws IOException {
    	
    	List<Path> backupFolders = getAllBackupFolders(backupFolder);
    	
    	String returnValue = null;
    	
    	// foldername for the date, without the "full" or "incremental" because we don't need this
    	SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.BACKUPFOLDERDATEFORMAT_STRING);
		String backupfoldernameJustTheDate = dateFormat.format(beforeDate);
    	
    	for (int i = 0; i < backupFolders.size();i++) {
    		String backupFolderName = backupFolders.get(i).getFileName().toString();
    		if (backupFolderName.compareTo(backupfoldernameJustTheDate) <= 0) {
    			return backupFolderName;
    		}
    	}
    	
    	return returnValue;
    }

    /**
     * get all backupFolders as array of strings, sorted by date, first element is the most recent
     * @param backupFolder source to search in
     * @param beforeBackupWithName search only backupfolders before beforeBackupWithName, we compare backup folder names here, they are chronologically sorted
     * @return
     * @throws IOException
     */
    public static List<String> getAllBackupFoldersAsStrings(Path backupFolder, String beforeBackupWithName) throws IOException {
    	
    	List<Path> backupFolders = getAllBackupFolders(backupFolder);
    	
    	List<String> returnvalue = new ArrayList<>();
    	
    	for (Path path: backupFolders) {
    		if (path.getFileName().toString().compareTo(beforeBackupWithName) < 0) {
    			returnvalue.add(path.getFileName().toString());
    		}
    		
    	}
    	
    	return returnvalue;
    	
    }


    private static List<Path> getAllBackupFolders(Path backupFolder) throws IOException {
        List<Path> backupFolders = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(backupFolder, entry ->
                Files.isDirectory(entry) && isValidBackupFolder(entry))) {
            for (Path entry : directoryStream) {
                backupFolders.add(entry);
            }
        }
        
    	// sort the backupFolder paths as per name of the last folder
    	// after sorting, the first element is the most recent backup
    	Collections.sort(backupFolders, (a,b) -> b.getFileName().toString().compareTo(a.getFileName().toString()));

        return backupFolders;
    }
    
    private static boolean isValidBackupFolder(Path folder) {
        String folderName = folder.getFileName().toString();
        return (folderName.contains("Full") || folderName.contains("Incremental"));
    }
}
