package utilities;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import Interfaces.ProcessText;
import model.AFile;
import model.AFileOrAFolder;
import model.AFolder;
import model.Constants;
import model.UIParameters;

public class CleanBackup {
	
	public static void cleanBackupFolders(ProcessText processText, List<String> backupFoldersToDelete, List<String> allBackupFoldersOlderThanAYearThatWillNotBeDeleted, String destination) {
		
		// NIET GOED /////
		
		// 
		
		// these are the steps
		// for each backup folder in the list of folders that will not be deleted
		// (named here 'current backup folder being processed'
		//  - parse the folderlist (folderlist.json)
		// 	- go through each file in the folderlist
		//     - check if the backupfolder for that file is in the list of listOfBackupsToDelete
		//     - if yes, copy the file to the current backup folder being processed
		//     - update also the folderlist
		//     - at the end save the udpated folderlist in the current backup folder being processed
		// do this for each folder in the list of folders that will not be deleted
		// at the end delete the folders in listOfBackupsToDelete
		
		Path destinationFolderPath = Paths.get(destination);
		
		for (String backupFolder : allBackupFoldersOlderThanAYearThatWillNotBeDeleted) {
            
			Path backupFolderPath = destinationFolderPath.resolve(backupFolder);
			
			System.out.println("Processing folder " + backupFolder);
			
            // convert folderlist.json to AFileOrAFolder
            AFileOrAFolder listOfFilesAndFoldersInBackupFolder = FileAndFolderUtilities.fromFolderlistDotJsonToAFileOrAFolder(backupFolderPath.resolve("folderlist.json"), processText);

            iterateThroughAFileOrAfolder(processText, listOfFilesAndFoldersInBackupFolder, backupFoldersToDelete, destinationFolderPath, backupFolderPath, Paths.get(""));
			
        }
		
	}
	
	/**
	 * if a file, then it will check if the backupfolder where the latest version is stored is in the list of folders to be deleted and if so copy it<br>
	 * if a folder, then recursively calls itself
	 * @param aFileOrAFolder instance of AFileOrAFolder
	 * @param listOfBackupsToDelete
	 * @param destinationFolderPath the dest folder where all backupfolders are stored
	 * @param backupFolderPathBeingProcessed backupFolder for which we currently check if the json has files that are stored in one of the backupfolders to delete
	 * @param the subfolder within backupFolderPathBeingProcessed, that is currently being processed
	 */
	private static void iterateThroughAFileOrAfolder(ProcessText processText, AFileOrAFolder aFileOrAFolder, List<String> listOfBackupsToDelete, Path destinationFolderPath, Path backupFolderPathBeingProcessed, Path subfolder) {
		
		if (aFileOrAFolder instanceof AFolder) {
			
			for (AFileOrAFolder aFileOrAFolder2 : ((AFolder)aFileOrAFolder).getFileOrFolderList()) {
				
				iterateThroughAFileOrAfolder(
						processText, 
						aFileOrAFolder2, 
						listOfBackupsToDelete, 
						destinationFolderPath, 
						backupFolderPathBeingProcessed, 
						subfolder.resolve(aFileOrAFolder2.getName()));
			}
			
		} else {
			
			String pathToBackup =  ((AFile)aFileOrAFolder).getPathToBackup();
			
			if (listOfBackupsToDelete.contains(pathToBackup)) {
				
				Path sourcePath = destinationFolderPath.resolve(pathToBackup).resolve(subfolder).resolve(pathToBackup).resolve(((AFile)aFileOrAFolder).getName());
				Path destinationPath = destinationFolderPath.resolve(backupFolderPathBeingProcessed).resolve(pathToBackup).resolve(((AFile)aFileOrAFolder).getName());
				
				System.out.println("about to copy");
				System.out.println("   " + sourcePath.getFileName().toString());
				System.out.println("   " + destinationPath.getFileName().toString());
				
				/*try {
					PathUtilities.copyFile(sourcePath, destinationPath, null);
				} catch (IOException e) {
					processText.process("      copy failed again. Exception occurred : ");
					processText.process(e.toString());
					Thread.currentThread().interrupt();return;
				}*/
				
			}
			
		}
		
	}

    /**
     * 
     * @param processText
     * @param listOfBackupsToDelete give an empty array list as input, when the function returns it will contain the backups to delete
     * @param allBackupFoldersOlderThanAYearList give an empty array list as input, when the function returns it will contain the backups older than a year
     */
    public static void getListOfBackupsToDelete(ProcessText processText, List<String> listOfBackupsToDelete, List<String> allBackupFoldersOlderThanAYearList) {

    	// first empty the two lists - you never know they may still contain data from a previous call
    	Iterator<String> iterator = listOfBackupsToDelete.iterator();
    	while (iterator.hasNext()) {
    	    iterator.next(); 
    	    iterator.remove(); 
    	}
    	iterator = allBackupFoldersOlderThanAYearList.iterator();
    	while (iterator.hasNext()) {
    	    iterator.next(); 
    	    iterator.remove(); 
    	}
    	
    	
    	// goal is to clean backups older than a year. 
    	// Function ListBackupsInFolder.getAllBackupFoldersAsStrings get all backup foldernames before a specific date
    	//     the data in that function is formatted as a backup foldername
    	//     so we get the date of year ago, formated as a backup foldername
    	//String aYearAgoAsString = (new SimpleDateFormat(Constants.BACKUPFOLDERDATEFORMAT_STRING)).format(new Date(new Date().getTime() - 365L * 24L * 3600L * 1000L));
    	String aYearAgoAsString = (new SimpleDateFormat(Constants.BACKUPFOLDERDATEFORMAT_STRING)).format(new Date(new Date().getTime() - 3L * 30L * 24L * 3600L * 1000L));

    	try {
    		
    		List<String> allBackupFoldersOlderThanAYearListNewList = ListBackupsInFolder.getAllBackupFoldersAsStrings(Paths.get(UIParameters.getInstance().getDestTextFieldTextString()), aYearAgoAsString);
    		
    		if (allBackupFoldersOlderThanAYearListNewList.size() == 0) {return;}
    		
    		// currentBackupFolderString is the backup folder that will not be deleted, and to which we compare subsequent folders
    		// initially it 's the first folder, after that it will be the next folder more than 30 days older
    		// we store the date off that folder
    		Date dateOfCurrentBackupFolderAsDate = OtherUtilities.getBackupDate(allBackupFoldersOlderThanAYearListNewList.getFirst(), processText);
    		allBackupFoldersOlderThanAYearList.add(allBackupFoldersOlderThanAYearListNewList.getFirst());
    		
    		for (String string : allBackupFoldersOlderThanAYearListNewList.subList(1, allBackupFoldersOlderThanAYearListNewList.size())) {
    			
				Date dateOfTheBackupDate = OtherUtilities.getBackupDate(string, processText);
				
				long differenceInMilliseconds = dateOfCurrentBackupFolderAsDate.getTime() - dateOfTheBackupDate.getTime();
				
				if ((differenceInMilliseconds / (1000 * 60 * 60 * 24) < 30) && !string.contains("Full")) {
					listOfBackupsToDelete.add(string);
				} else {
					allBackupFoldersOlderThanAYearList.add(string);
					dateOfCurrentBackupFolderAsDate = dateOfTheBackupDate;
				}
				
			}
    	
    	} catch (IOException e) {
			processText.process("Exception in getListOfBackupsToDelete");
            processText.process(e.toString());
            Thread.currentThread().interrupt();
            return;
		}
    	
    	return;
    	
    }


}
