package main;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import Interfaces.ProcessText;
import Interfaces.FolderChangedHolder;
import Interfaces.TextFieldChanged;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.UIParameters;
import utilities.ListBackupsInFolder;
import utilities.UIUtilities;

public class SectionRestoreParameters {

	// there's these parameters
	// - backup folder to use (which sets also the date to restore)
	//     - also button to select the folder
	// - folder to restore to
	// - subfolder to restore
	
	private static VBox completeSelectBackupVBox;
	private static HBox selectBackupHBox;
	private static Button selectBackupButton;
	private static Label selectedBackupLabel;
	
	private static VBox completeRestoreToFolderVBox;
	private static HBox restoreToFolderHBoxWithFileText;
	private static HBox restoreToFolderHBoxWithLabelHBox;
	private static Label restoreToFolderWarningLabel;
	
	private static ProcessText processText;
	
	private static List<String> allBackups;

    static public String defaultBackupFolderTextString = "Geen backup geselecteerd";

    /**
     * 
     * @param primaryStage
     * @param processText
     * @param restoreToFolderChanged
     * @param initialTextRestoreToFolder
     * @param destFolderChangedHolder
     * @param backupFolderChangedHolder is used to update UIParameters
     * @return
     */
	public static VBox createSectionRestoreParameters(Stage primaryStage, ProcessText processText, TextFieldChanged restoreToFolderChanged, String initialTextRestoreToFolder, FolderChangedHolder destFolderChangedHolder, FolderChangedHolder backupFolderChangedHolder) {
		
        // texts for excluced path list
        String labelTextSelectRestoreToFolder = "Selecteer folder waar de herstelde mappen moeten komen\u002A:\n";
        String labelTextSelectWithExplanationExcludedPathList = "nog toe te voegen\n" 
        		+ "";
        String labelSelectBackup = "Selecteer de backup\u002A:";
        String labelSelectBackupWithExplanation = ";";

		SectionRestoreParameters.processText = processText;

		backupFolderChangedHolder.folderChanged = (String folder) -> backupFolderChangedHandler(folder);
		
		destFolderChangedHolder.folderChanged = (String folder) -> destFolderChangedHandler(folder);

		VBox section = new VBox();
        section.setSpacing(5);

		completeSelectBackupVBox = new VBox();
		completeRestoreToFolderVBox = new VBox();
        
		//////////////////////////////////////////////
        //////         CREATE BACKUP VBOX
		//////////////////////////////////////////////
        
		// create label which gives explanation
		/// the label to explain that source needs to be given
        Label label = new Label(labelSelectBackup);
        UIUtilities.addToolTip(label, labelSelectBackup + labelSelectBackupWithExplanation);
		
		// create button to select the backup folder
		selectBackupButton = new Button("Kies");
		
		// create the label that shows the currently selected backup folder
		selectedBackupLabel = new Label();
		selectedBackupLabel.setText(defaultBackupFolderTextString);

		// create the HBOX that holds the Button selectBackupButton and the Label selectedBackupLabel
		selectBackupHBox = new HBox();
		selectBackupHBox.setSpacing(10);//  space between individual nodes in the Hbox
		selectBackupHBox.setAlignment(Pos.CENTER_LEFT);
	    GridPane.setHgrow(selectBackupHBox, Priority.ALWAYS);
		selectBackupHBox.getChildren().addAll(label, selectBackupButton, selectedBackupLabel);
		
		// create the complete HBOX that holds the backup info : completeSelectBackupVBox
		completeSelectBackupVBox.getChildren().add(selectBackupHBox);
        // add completeSelectBackupVBox to section
		section.getChildren().add(completeSelectBackupVBox);
		
		//////////////////////////////////////////////
		///////        CREATE RESTORETO VBOX
		//////////////////////////////////////////////
		
		restoreToFolderHBoxWithFileText = UIUtilities.createHBoxToSelectFolder(primaryStage, labelTextSelectRestoreToFolder, labelTextSelectWithExplanationExcludedPathList, restoreToFolderChanged, initialTextRestoreToFolder);
		restoreToFolderHBoxWithFileText.setSpacing(10);
		
		// create the complete HBOX that holds the RESTORETO info : completeRestoreToFolderVBox
		completeRestoreToFolderVBox.getChildren().add(restoreToFolderHBoxWithFileText);
        section.getChildren().add(completeRestoreToFolderVBox);

		
        // intialize label and hbox that will contain the warning label, but don't add it yet
        restoreToFolderWarningLabel = new Label();
        restoreToFolderWarningLabel.setStyle("-fx-text-fill: red;");
        restoreToFolderHBoxWithLabelHBox = new HBox();
        restoreToFolderHBoxWithLabelHBox.getChildren().add(restoreToFolderWarningLabel);
		
		return section;
		
	}
	
    public static void addRestoreToFolderWarning(String text) {UIUtilities.addWarningToVBox(completeRestoreToFolderVBox, restoreToFolderHBoxWithLabelHBox, text, restoreToFolderWarningLabel);}

	public static void getAllBackupDates(Path backupPath) {
		
		try {
			
			allBackups = ListBackupsInFolder.getAllBackupFoldersAsStrings(backupPath, "zzzz");
			
		} catch (IOException e) {
			if (processText != null) {
				processText.process("Fout bij het uitlezen van backup folders.");
				processText.process(e.toString());
			} else {
				System.out.println("Fout bij het uitlezen van backup folders.");
				System.out.println(e.toString());
			}
			System.exit(1);
		}
		
	}
	
	/**
	 * if user selects different backup folder from where restore is done,  then this function is called
	 * @param newFolder
	 */
	private static void backupFolderChangedHandler(String newFolder) {
		UIParameters.getInstance().setBackupFolderName(newFolder);
	}
	
	private static void destFolderChangedHandler(String newFolder) {
		getAllBackupDates(Paths.get(newFolder));
	}
	
}
