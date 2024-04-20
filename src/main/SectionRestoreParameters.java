package main;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import Interfaces.ProcessText;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import utilities.ListBackupsInFolder;

public class SectionRestoreParameters {

	private static VBox completeSelectBackupVBox;
	private static HBox selectBackupHBox;
	private static Button selectBackupButton;
	private static Label selectedBackupLabel;
	private static ProcessText processText;
	
	private static List<String> allBackups;
	
	/*public static VBox createSectionRestoreParameters(ProcessText processText) {
		SectionRestoreParameters.processText = processText;
	}*/
	
	public static void getAllBackupDates(Path backupPath) {
		
		try {
			
			allBackups = ListBackupsInFolder.getAllBackupFoldersAsStrings(backupPath, "zzzz");
			
		} catch (IOException e) {
			processText.process("Fout bij het uitlezen van backup folders.");
			processText.process(e.toString());
		}
		
	}
	
}
