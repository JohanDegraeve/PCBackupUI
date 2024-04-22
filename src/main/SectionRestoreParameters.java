package main;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import Interfaces.ProcessText;
import Interfaces.FolderChangedHolder;
import Interfaces.TextFieldChanged;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;
import model.Constants;
import model.UIParameters;
import utilities.ListBackupsInFolder;
import utilities.OtherUtilities;
import utilities.UIUtilities;

public class SectionRestoreParameters {

	/**
	 * used in pop up with list of backup foldrs, initiallyempty
	 */ 
	private static ObservableList<String> allBackups = FXCollections.observableArrayList();

	// there's these parameters
	// - backup folder to use (which sets also the date to restore)
	//     - also button to select the folder
	// - folder to restore to
	// - subfolder to restore
	
	private static VBox completeSelectBackupVBox;
	private static HBox selectBackupHBox;
	private static Button selectBackupButton;
	private static Label selectedBackupLabel;
	private static Popup popup = new Popup();// pop up to let user select from list of backups
	//private static ListView<String> listView = new ListView<>(allBackups);
	
	private static VBox completeRestoreToFolderVBox;
	private static HBox restoreToFolderHBoxWithFileText;
	private static HBox restoreToFolderHBoxWithLabelHBox;
	private static Label restoreToFolderWarningLabel;
	
	private static ProcessText processText;
	
    static public String defaultBackupFolderTextString = "Geen backup geselecteerd";
    
    static private Stage stage;

    /**
     * 
     * @param primaryStage
     * @param processText
     * @param restoreToFolderChanged
     * @param initialTextRestoreToFolder
     * @param destFolderChangedHolder is the folder where backup subfolders are stored, that folder is needed here because we get all backup folder names
     * @param backupFolderChangedHolder is the subfolder within destFolder that the user selected as backup folder to restore from
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
		
		stage = primaryStage;

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

	
	/**
	 * if user selects different backup folder from where restore is done, then this function is called<br>
	 * The function stores the new folder in UIParameters
	 * @param newFolder
	 */
	private static void backupFolderChangedHandler(String newFolder) {
		UIParameters.getInstance().setBackupFolderName(newFolder);
	}
	
	/**
	 * function gets all backup subfolders and stores the list in allBackups
	 * @param newFolder
	 */
	private static void destFolderChangedHandler(String newFolder) {
		attachPopUpToSelectBackupButton(newFolder);
	}
	
	private static void attachPopUpToSelectBackupButton(String newFolder) {
		
		/// zzz is used because we want all backups
		try {
			List<String> backupFokdersAsStrings = ListBackupsInFolder.getAllBackupFoldersAsStrings(Paths.get(newFolder), "zzzz");
			List<String>  backupFokdersAsDates = new ArrayList<>();
			for (String string : backupFokdersAsStrings) {
				backupFokdersAsDates.add(OtherUtilities.dateToString(OtherUtilities.getBackupDate(string, processText),"yyyy MMM dd HH:mm" ));
			}
			allBackups = FXCollections.observableArrayList(backupFokdersAsDates);
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
		ListView<String> listView = new ListView<>(allBackups);
		listView.setOnMouseClicked(e -> {
            // Handle selection of an item
            String selectedString = listView.getSelectionModel().getSelectedItem();
            System.out.println("Selected String: " + selectedString);
            // You can store the selectedString in a variable or perform any other action here
            popup.hide(); // Close the popup
        });
		selectBackupButton.setOnAction(e -> popup.show(stage));
		
		// Create a cancel button
        Button cancelButton = new Button("Annuleer");
        cancelButton.setOnAction(e -> popup.hide());
		
        // Create a Pane to contain the cancel button
        Pane cancelButtonContainer = new Pane(cancelButton);
        cancelButtonContainer.setMinWidth(200); // Match the width of the ListView
        cancelButtonContainer.setStyle("-fx-background-color: white; -fx-border-color: #0077CC; -fx-border-width: 2px;");

        // Set the position of the cancel button within the container
        cancelButton.layoutXProperty().bind(cancelButtonContainer.widthProperty().subtract(cancelButton.widthProperty()).divide(2));
        cancelButton.layoutYProperty().bind(cancelButtonContainer.heightProperty().subtract(cancelButton.heightProperty()).divide(2));

        // Create a layout for the popup content
        VBox popupContent = new VBox(listView, cancelButtonContainer);
        popupContent.setSpacing(10); // Set spacing between nodes
        
        popup.getContent().addAll(popupContent);
        
	}
	
}
