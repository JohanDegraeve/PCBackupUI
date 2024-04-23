package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;

import Enumerations.Action;

/**
 * to store parameters given in UI in a file and read them in a next test session<br>
 * Not all parameters are stored in file:<br>
 * Not stored:<br>
 * - currentlySelectedActionKey<br>
 * - backupFolderName<br> 
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
    
    // restore parameters
    /*
     * to which folder should restore be written
     */
    private String restoreToFolderName = "";
    /**
     * the backup folder to use, this is the folder within the dest folder
     */
    private String backupFolderName = "";
    /**
     * subfolder to restore
     */
    private String subfolderToRestore = "";
    
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

	public String getSubfolderToRestore() {
		return subfolderToRestore;
	}

	public void setSubfolderToRestore(String subfolderToRestore) {
		this.subfolderToRestore = subfolderToRestore;
	}

	// Create a HashMap to store key-value pairs
    private static HashMap<String, String> keyValueMap = new HashMap<>();

	
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
