/*
 * Copyright 2024 Johan Degraeve
 *
 * This file is part of PCBackupUI.
 *
 * PCBackup is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PCBackupUI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PCBackupUI. If not, see <https://www.gnu.org/licenses/>.
 */
package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;

import Enumerations.Action;

/**
 * to store parameters given in UI in a file and read them in a next test session<br>
 * Not all parameters are stored in file:<br>
 * Not stored:<br>
 * - currentlySelectedActionKey<br>
 * - backupFolderName<br> 
 * - folderToRestore<br>
 */
public class UIParameters {
	
	private static String parameterFileNameString = "pcbackup-parameters.txt";

	static UIParameters instance;

    // selected action
    private String currentlySelectedActionKey = "currentlySelectedActionKey";

    // Attributes for Section with source, dest and logfile folder  ...
    private String sourceTextFieldTextKey = "sourceTextFieldTextKey";
	private String destTextFieldTextKey = "destTextFieldTextKey";
    private String logfileFolderTextFieldKey = "logfileFolderTextFieldKey";
    
    // additional Attributes needed for backup
    private String excludedFileListTextFieldTextKey = "excludedFileListTextFieldTextKey";
    private String excludedPathListTextFieldTextKey = "excludedPathListTextFieldTextKey";
    private String folderNameMappingTextFieldTextKey = "folderNameMappingTextFieldTextKey";

    // additional attributes needed for search
    private String writeSearchToFolderTextKey = "writeSearchToFolderTextKey";

    // restore parameters
    /**
     * to which folder should restore be written
     */
    private String restoreToFolderName = "";
    
    /**
     * the backup folder to use, this is the folder within the dest folder
     */
    private String backupFolderName = "";
    
    /**
     * folder to restore
     */
    private String folderToRestore = "";
    
    // search parameters
    private String searchText1 = "";
	private String searchText2 = "";
    private String searchText3 = "";
    private Date startSearchDate = null;
	private Date endSearchDate = null;
	
	public Action getCurrentlySelectedAction() {
    	return Action.stringToEnum(keyValueMap.get(currentlySelectedActionKey));
	}

	public void setCurrentlySelectedAction(Action action) {
		
		if (action == null) {
			if (keyValueMap.containsKey(currentlySelectedActionKey)) {
				keyValueMap.remove(currentlySelectedActionKey);
			}
		} else {
			keyValueMap.put(currentlySelectedActionKey, action.getStringValue());
		}
		
		writeHashMapToFile();
	}

	public String getSourceTextFieldTextString() {
		return keyValueMap.get(sourceTextFieldTextKey);
	}

	public void setSourceTextFieldTextString(String sourceTextFieldTextString) {
		storeInKeyValueMap(sourceTextFieldTextKey, sourceTextFieldTextString);
	}

	public String getDestTextFieldTextString() {
		return keyValueMap.get(destTextFieldTextKey);
	}

	public void setDestTextFieldTextString(String destTextFieldTextString) {
		storeInKeyValueMap(destTextFieldTextKey, destTextFieldTextString);
	}

	public String getLogfileFolderTextFieldString() {
		return keyValueMap.get(logfileFolderTextFieldKey);
	}

	public void setLogfileFolderTextFieldString(String logfileFolderTextFieldString) {
		storeInKeyValueMap(logfileFolderTextFieldKey, logfileFolderTextFieldString);
	}

	public String getExcludedFileListTextFieldTextString() {
		return keyValueMap.get(excludedFileListTextFieldTextKey);
	}

	public void setExcludedFileListTextFieldTextString(String excludedFileListTextFieldTextString) {
		storeInKeyValueMap(excludedFileListTextFieldTextKey, excludedFileListTextFieldTextString);
	}

	public String getExcludedPathListTextFieldTextString() {
		return keyValueMap.get(excludedPathListTextFieldTextKey);
	}

	public void setExcludedPathListTextFieldTextString(String excludedPathListTextFieldTextString) {
		storeInKeyValueMap(excludedPathListTextFieldTextKey, excludedPathListTextFieldTextString);
	}

	public String getFolderNameMappingTextFieldTextString() {
		return keyValueMap.get(folderNameMappingTextFieldTextKey);
	}

	public void setFolderNameMappingTextFieldTextString(String folderNameMappingTextFieldTextString) {
		storeInKeyValueMap(folderNameMappingTextFieldTextKey, folderNameMappingTextFieldTextString);
	}
	
	public String getRestoreToFolderName() {
		return restoreToFolderName;
	}

	public void setRestoreToFolderName(String backupFolderName) {
		this.restoreToFolderName = backupFolderName;
	}
	
    public String getBackupFolderName() {
		return backupFolderName;
	}

	public void setBackupFolderName(String backupFolderName) {
		this.backupFolderName = backupFolderName;
	}

	public String getFolderToRestore() {
		return folderToRestore;
	}

	public void setFolderToRestore(String folderToRestore) {
		this.folderToRestore = folderToRestore;
	}

    public String getSearchText1() {
		return searchText1;
	}

	public void setSearchText1(String searchText1) {
		this.searchText1 = searchText1;
	}

	public String getSearchText2() {
		return searchText2;
	}

	public void setSearchText2(String searchText2) {
		this.searchText2 = searchText2;
	}

	public String getSearchText3() {
		return searchText3;
	}

	public void setSearchText3(String searchText3) {
		this.searchText3 = searchText3;
	}

	// Create a HashMap to store key-value pairs
    private static HashMap<String, String> keyValueMap = new HashMap<>();


	public Date getEndSearchDate() {
		return endSearchDate;
	}

	public void setEndSearchDate(Date endSearchDate) {
		this.endSearchDate = endSearchDate;
	}

	public Date getStartSearchDate() {
		return startSearchDate;
	}

	public void setStartSearchDate(Date startSearchDate) {
		this.startSearchDate = startSearchDate;
	}
	
	public String getWriteSearchToFolderTextString() {
		return keyValueMap.get(writeSearchToFolderTextKey);
	}

	public void setWriteSearchToFolderTextString(String writeSearchToFolderTextString) {
		storeInKeyValueMap(writeSearchToFolderTextKey, writeSearchToFolderTextString);
	}


	// private constructor to avoid creation
	private UIParameters() {
		
	}
	
	public static UIParameters getInstance() {
		if (instance !=  null) {
			return instance;
		}
		
		instance = new UIParameters();
		
        try {
        	
            BufferedReader reader = new BufferedReader(new FileReader(createParameterFileFullPathAsString()));
            
            String line;

            // Read file line by line
            while ((line = reader.readLine()) != null) {
                // Split the line by "==="
                String[] parts = line.split("===");
                
                // Check if there are exactly two parts
                if (parts.length == 2) {
                    // Add key-value pair to the HashMap
                    keyValueMap.put(parts[0], parts[1]);
                } else {
                    // Handle invalid lines
                    System.err.println("Invalid line: " + line);
                }
            }

            // Close the reader
            reader.close();
            
        } catch (IOException e) {
            // the file doesn't exist, means app never started before
        }
		
		return instance;
		
	}
	
	private static void writeHashMapToFile() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(createParameterFileFullPathAsString()));

            // Iterate over the HashMap and write each key-value pair to the file
            for (String key : keyValueMap.keySet()) {
                String line = key + "===" + keyValueMap.get(key);
                writer.write(line);
                writer.newLine(); // Add newline after each pair
            }

            // Close the writer
            writer.close();
        } catch (IOException e) {
        	// TODO pop up met error
            e.printStackTrace();
        }
    }
	
	private static String createParameterFileFullPathAsString() {
		return Paths.get(System.getProperty("user.dir")).resolve(parameterFileNameString).toString();
	}
	
	/**
	 * verifies if value is null or "" and if yes removes key from keyValueMap<br>
	 * if not nul, stores in keyValueMap<br>
	 * calls writeHashMapToFile
	 */
	private void storeInKeyValueMap(String key, String value) {
		if (value == null || value.length() == 0) {
			if (keyValueMap.containsKey(key)) {
				keyValueMap.remove(key);
			}
		} else {
			keyValueMap.put(key, value);
		}
		writeHashMapToFile();
	}
}
