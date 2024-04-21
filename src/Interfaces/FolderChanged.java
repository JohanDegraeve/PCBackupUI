package Interfaces;

@FunctionalInterface
public interface FolderChanged {
	/**
	 * to trigger that user has changed a folder
	 * @param  the new folder
	 */
	void handleNewFolder(String folderString);
	
}

