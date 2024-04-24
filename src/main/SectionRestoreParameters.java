package main;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import Interfaces.ProcessText;
import Interfaces.BackupFolderSelectedHandler;
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
	
	private static VBox completeRestoreToFolderVBox;
	private static HBox restoreToFolderHBoxWithFileText;
	private static HBox restoreToFolderHBoxWithLabelHBox;
	private static Label restoreToFolderWarningLabel;
	
	private static ProcessText processText;
	
    static final public String defaultBackupFolderTextString = "Geen backup geselecteerd";
    
    static private BackupFolderSelectedHandler backupFolderSelectedHandler = null;
    
    static private Stage stage;

    /**
     * 
     * @param primaryStage
     * @param processText
     * @param restoreToFolderChanged
     * @param initialTextRestoreToFolder
     * @param destFolderChangedHolder is the folder where backup subfolders are stored, that folder is needed here because we get all backup folder names
     * @param backupFolderChangedHolder is the subfolder within destFolder that the user selected as backup folder to restore from
     * @param BackupFolderSelectedHandler  
     * @return
     */
	public static VBox createSectionRestoreParameters(Stage primaryStage, ProcessText processText, TextFieldChanged restoreToFolderChanged, String initialTextRestoreToFolder, FolderChangedHolder destFolderChangedHolder, BackupFolderSelectedHandler backupFolderSelectedHandler) {
		
        // texts for excluced path list
        String labelTextSelectRestoreToFolder = "Selecteer folder waar de herstelde mappen moeten komen\u002A:\n";
        String labelTextSelectRestoreFolderWithExplanation = "De bestanden uit de backup worden gekopieerd naar deze map.\n" 
        						+ "Let op: als er al mappen en bestanden bestaan in deze map dan worden ze mogelijk overschreven.\n" 
        		                + "Je kunt hier dezelfde map kiezen als de map met de oorspronkelijk bestanden,\n"
        		                + "maar dan zorg je best dat de overeenkomende map die hersteld wordt in de oorspronkelijke map, verwijderd wordt.";

        String labelSelectBackup = "Selecteer de backup\u002A:";
        String labelSelectBackupWithExplanation = "Selecteer welke backup je wil gebruiken om de restore te doen.\n" + 
        		"Indien de knop niet actief is, dan betekent het dat er in de backup map geen backups werden gevonden.\n" +
        		"Controleer in dat geval het veld 'Waar bevinden zich de backups'.\n";

		SectionRestoreParameters.processText = processText;

		destFolderChangedHolder.folderChanged = (String folder) -> destFolderChangedHandler(folder);
		
		SectionRestoreParameters.backupFolderSelectedHandler = backupFolderSelectedHandler;
		
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
		
		restoreToFolderHBoxWithFileText = UIUtilities.createHBoxToSelectFolder(primaryStage, labelTextSelectRestoreToFolder, labelTextSelectRestoreFolderWithExplanation, restoreToFolderChanged, initialTextRestoreToFolder);
		restoreToFolderHBoxWithFileText.setSpacing(10);
		
		// create the complete HBOX that holds the RESTORETO info : completeRestoreToFolderVBox
		completeRestoreToFolderVBox.getChildren().add(restoreToFolderHBoxWithFileText);
        section.getChildren().add(completeRestoreToFolderVBox);

		
        // intialize label and hbox that will contain the warning label, but don't add it yet
        restoreToFolderWarningLabel = new Label();
        restoreToFolderHBoxWithLabelHBox = new HBox();
        restoreToFolderHBoxWithLabelHBox.getChildren().add(restoreToFolderWarningLabel);
		
		return section;
		
	}
	
    public static void addRestoreToFolderWarning(String text) {
    	restoreToFolderWarningLabel.setStyle("-fx-text-fill: red;");
    	UIUtilities.addWarningToVBox(completeRestoreToFolderVBox, restoreToFolderHBoxWithLabelHBox, text, restoreToFolderWarningLabel);
    }
    
    public static void addRestoreToFolderInfo(String text) {
    	restoreToFolderWarningLabel.setStyle("-fx-text-fill: green;");
    	UIUtilities.addWarningToVBox(completeRestoreToFolderVBox, restoreToFolderHBoxWithLabelHBox, text, restoreToFolderWarningLabel);
    }

	
	/**
	 * function gets all backup subfolders and stores the list in allBackups
	 * @param newFolder
	 */
	private static void destFolderChangedHandler(String newFolder) {
		attachPopUpToSelectBackupButton(newFolder);
	}
	
	private static void attachPopUpToSelectBackupButton(String newFolder) {
		
		if (newFolder == null || newFolder.length() == 0) {
			selectedBackupLabel.setText(defaultBackupFolderTextString);
			selectBackupButton.setDisable(true);
			return;
		}
		
		try {
			List<String> backupFokdersAsStrings = ListBackupsInFolder.getAllBackupFoldersAsStrings(Paths.get(newFolder), "zzzz");
			List<String>  backupFokdersAsDates = new ArrayList<>();
			for (String string : backupFokdersAsStrings) {
				backupFokdersAsDates.add(OtherUtilities.dateToString(OtherUtilities.getBackupDate(string, processText),"dd MMM yyyy   HH:mm" ));
			}
			allBackups = FXCollections.observableArrayList(backupFokdersAsDates);
		
			ListView<String> listView = new ListView<>(allBackups);
			listView.setOnMouseClicked(e -> {
	            // Handle selection of an item
	            String selectedString = listView.getSelectionModel().getSelectedItem();
	            
	            String selectedBackupFolderString = backupFokdersAsStrings.get(listView.getSelectionModel().getSelectedIndex());
	            
	            // set the selected backup in the label
	            selectedBackupLabel.setText(selectedString);
	            
	            if (backupFolderSelectedHandler != null) {
	            	backupFolderSelectedHandler.handleSelectedBackupFolder(selectedBackupFolderString);
	            }
	            
	            // You can store the selectedString in a variable or perform any other action here
	            popup.hide(); // Close the popup
	        });
			
			selectBackupButton.setOnAction(e -> popup.show(stage));
			
			if (allBackups.size() > 0) {
				selectBackupButton.setDisable(false);
				if (!allBackups.contains(selectedBackupLabel.getText())) {
					selectedBackupLabel.setText(defaultBackupFolderTextString);
					if (backupFolderSelectedHandler != null) {
		            	backupFolderSelectedHandler.handleSelectedBackupFolder(defaultBackupFolderTextString);
		            }
				}
				
			} else {
				selectBackupButton.setDisable(true);
				selectedBackupLabel.setText(defaultBackupFolderTextString);
				if (backupFolderSelectedHandler != null) {
	            	backupFolderSelectedHandler.handleSelectedBackupFolder(defaultBackupFolderTextString);
	            }
			}
			
			
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
        
		} catch (IOException e) {
			if (processText != null) {
				processText.process("Fout bij het uitlezen van backup folders.");
				processText.process(e.toString());
			} else {
				System.out.println("Fout bij het uitlezen van backup folders.");
			}
			System.exit(1);
		}
        
	}
	
}
