package Interfaces;

/**
 * interface for passing the backup selected by the user<br>
 * 
 * Note that interfaces ProcessText and FolderChanged could also be used, but it makes it a bit more readable to have a class with a name that clarifies the goal
 */
public interface BackupFolderSelectedHandler {
	void handleSelectedBackupFolder(String backup);
}
