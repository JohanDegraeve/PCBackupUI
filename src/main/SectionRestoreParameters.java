package main;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.Stage;
import model.AFile;
import model.AFileOrAFolder;
import model.AFolder;
import model.UIParameters;
import utilities.FileAndFolderUtilities;
import utilities.ListBackupsInFolder;
import utilities.OtherUtilities;
import utilities.UIUtilities;

public class SectionRestoreParameters {

	private static final int selectFolterToRestorePopupWidth = 1000;
	private static final int selectBackupPopupWidth = 200;
	
	// there's these parameters
	// - backup folder to use (which sets also the date to restore)
	//     - also button to select the folder
	// - folder to restore to
	// - subfolder to restore
	
	private static VBox completeSelectBackupVBox;
	private static HBox selectBackupHBox;
	private static Button selectBackupButton;
	private static Label selectedBackupLabel;
	private static Popup selectBackupPopup = new Popup();// pop up to let user select from list of backups
	
	private static VBox completeRestoreToFolderVBox;
	private static HBox restoreToFolderHBoxWithFileText;
	private static HBox restoreToFolderHBoxWithLabelHBox;
	private static Label restoreToFolderWarningLabel;
	
	private static VBox completeSelectFolderToRestoreBox;
	private static HBox selectFolderToRestoreHBox;
	private static Button selectFolderToRestoreButton;
	private static Label selectFolderToRestoreLabel;
	private static Popup selectFolderToRestorePopup = new Popup();
	
	private static ProcessText processText;
	
    public static final String defaultBackupFolderTextString = "Geen backup geselecteerd";
    
    public static final String defaultFolderToRestoreTextString = "Geen map geselecteerd";
    
    private static BackupFolderSelectedHandler backupFolderSelectedHandler = null;
    
    private static Stage stage;
    
	/**
	 * used in pop up with list of backup folders, initially empty
	 */ 
	private static ObservableList<String> allBackups = FXCollections.observableArrayList();

    /**
     * when aFolder is selected, then we add the parentFolder to this arraylist<br>
     * User can go up a folder, then we go back to the latest entry in that list
     */
    private static List<AFolder> parentFolders = new ArrayList<>();
    
    /**
     * as only Folders are clickable (we don't allow to select files for restore), we need to know how much folders there are.<br>
     * If user will click a line higher, no reaction
     */
    private static int amountOfFoldersInAFolder;
    
    /**
     * as it's displayed to the user, to keep track of the currently selected folder
     */
    private static String currentlySelectFolderToRestoreAString = "";
    
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

        String labelSelecFolderToRestore = "Selecteer de te herstellen map\u002A:";
        String labelSelectFolderToRestoreWithExplanation = "";

		SectionRestoreParameters.processText = processText;

		destFolderChangedHolder.folderChanged = (String folder) -> destFolderChangedHandler(folder);
		
		SectionRestoreParameters.backupFolderSelectedHandler = backupFolderSelectedHandler;
		
		stage = primaryStage;

		VBox section = new VBox();
        section.setSpacing(5);

		completeSelectBackupVBox = new VBox();
		completeRestoreToFolderVBox = new VBox();
		completeSelectFolderToRestoreBox = new VBox();
        
		//////////////////////////////////////////////
        //////         CREATE BACKUP VBOX
		//////////////////////////////////////////////
        
		// create label which gives explanation
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
		///////        CREATE FOLDERTORESTORE VBOX
		//////////////////////////////////////////////
		
		// create label which gives explanation
        label = new Label(labelSelecFolderToRestore);
        UIUtilities.addToolTip(label, labelSelecFolderToRestore + labelSelectFolderToRestoreWithExplanation);
		
		// create button to select the backup folder
		selectFolderToRestoreButton = new Button("Kies");
		selectFolderToRestoreButton.setDisable(true);
		
		// create the label that shows the currently selected folder to restore
		selectFolderToRestoreLabel = new Label();
		selectFolderToRestoreLabel.setText(defaultFolderToRestoreTextString);

		// create the HBOX that holds the Button selectFolderToRestoreButton and the Label selectFolderToRestoreLabel
		selectFolderToRestoreHBox = new HBox();
		selectFolderToRestoreHBox.setSpacing(10);//  space between individual nodes in the Hbox
		selectFolderToRestoreHBox.setAlignment(Pos.CENTER_LEFT);
	    GridPane.setHgrow(selectFolderToRestoreHBox, Priority.ALWAYS);
	    selectFolderToRestoreHBox.getChildren().addAll(label, selectFolderToRestoreButton, selectFolderToRestoreLabel);
		
		// create the complete HBOX that holds the backup info : completeSelectFolderToRestoreBox
	    completeSelectFolderToRestoreBox.getChildren().add(selectFolderToRestoreHBox);
        // add completeSelectFolderToRestoreBox to section
		section.getChildren().add(completeSelectFolderToRestoreBox);
		

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
			List<String> backupFoldersAsStrings = ListBackupsInFolder.getAllBackupFoldersAsStrings(Paths.get(newFolder), "zzzz");
			List<String> backupFoldersAsDates = new ArrayList<>();
			for (String string : backupFoldersAsStrings) {
				backupFoldersAsDates.add(OtherUtilities.dateToString(OtherUtilities.getBackupDate(string, processText),"dd MMM yyyy   HH:mm" ));
			}
			allBackups = FXCollections.observableArrayList(backupFoldersAsDates);
		
			ListView<String> listView = new ListView<>(allBackups);
			listView.setOnMouseClicked(e -> {
	            
				// only double click is treated
				if (!(e.getButton() == MouseButton.PRIMARY) || !(e.getClickCount() == 2)) {return;}
				
				String selectedString = listView.getSelectionModel().getSelectedItem();
	            
	            String selectedBackupFolderString = backupFoldersAsStrings.get(listView.getSelectionModel().getSelectedIndex());
	            
	            // set the selected backup in the label
	            selectedBackupLabel.setText(selectedString);
	            
	            attachPopUpToSelectFolderToRestoreButton(selectedBackupFolderString);
	            
	            if (backupFolderSelectedHandler != null) {
	            	backupFolderSelectedHandler.handleSelectedBackupFolder(selectedBackupFolderString);
	            }
	            
	            selectBackupPopup.hide();
	            
	        });
			
			selectBackupButton.setOnAction(e -> selectBackupPopup.show(stage));
			
			// if the allBackups is empty or the current text in selectedBackupLabel is not in the  list of allBackups
			//    then set back the selectedBackupLabel to the default value
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

			// Create a layout for the popup content, add cancel button
	        VBox popupContent = new VBox(listView, UIUtilities.createCancelButtonContainer(selectBackupPopup, selectBackupPopupWidth, "Annuleer"));
	        popupContent.setSpacing(10); // Set spacing between nodes
	        
	        selectBackupPopup.getContent().addAll(popupContent);
        
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
	
	/**
	 * this function uses the selecedBackupFolder (which is something like 2024-03-10 20;31;55 (Incremental))<br>
	 * It will open the file folderlist.json in that folder, parse it and then call attachPopUpToSelectFolderToRestoreButton(AFolder)<br>
	 * <br>
	 * Only used when backupFolder is changed by the user
	 * @param selecedBackupFolder
	 */
	private static void attachPopUpToSelectFolderToRestoreButton(String selecedBackupFolder) {
		
		if (selecedBackupFolder == null || selecedBackupFolder.length() == 0) {
			selectFolderToRestoreButton.setText(defaultFolderToRestoreTextString);
			selectFolderToRestoreButton.setDisable(true);
			return;
		}
		
		Path backupFolderPath = Paths.get(UIParameters.getInstance().getDestTextFieldTextString()).resolve(selecedBackupFolder);
		
        AFileOrAFolder listOfFilesAndFoldersInBackupFolder = FileAndFolderUtilities.fromFolderlistDotJsonToAFileOrAFolder
        		(backupFolderPath.resolve("folderlist.json"), processText);
        
        if(!(listOfFilesAndFoldersInBackupFolder instanceof AFolder)) {
        	processText.process("Blijkbaar is listOfFilesAndFoldersInBackupFolder geen folder");
        } else {
        	attachPopUpToSelectFolderToRestoreButton((AFolder)listOfFilesAndFoldersInBackupFolder);
        }

	}
	
	private static void attachPopUpToSelectFolderToRestoreButton(AFolder aFolder) {
		
		// if aFolder is null or if there are no files and/or folders in aFolder then return - excep tif parentFolder is not null
		//    if parentFolder, then even if there's no files, we allow the user to go a level up
		if (aFolder == null || (aFolder.getFileOrFolderList().size() == 0 && parentFolders.size() == 0)) {
			selectFolderToRestoreLabel.setText(defaultFolderToRestoreTextString);
			selectFolderToRestoreLabel.setDisable(true);
			return;
		}
		
		// create a list of all folders in aFolder
		// we maintain a second list of ints, which are indexes of each folder in  allFoldersInAFolder
		// this list will be sorted together with allFoldersInAFolder, so that we know where to find a folder with 
		//   a specific name in the original aFolder
		List<String> allFoldersInAFolder = new ArrayList<>();
		List<Integer> allFoldersInAFolderIndex = new ArrayList<>();
		for (int i = 0; i < aFolder.getFileOrFolderList().size(); i++) {
			AFileOrAFolder aFileOrAFolder = aFolder.getFileOrFolderList().get(i);
			if (aFileOrAFolder instanceof AFolder) {
				allFoldersInAFolder.add(aFileOrAFolder.getName());
				allFoldersInAFolderIndex.add(i);
			}
		}
		
		// store amount of folders
		// add 1 if parentFolder != null, because we will add ".." in that case
		amountOfFoldersInAFolder = allFoldersInAFolder.size() + (parentFolders.size() != 0 ? 1:0);

		// sort alphabetically, also sort allFoldersInAFolderIndex so that we keep track of the original index in aFolder.getFileOrFolderList()
		OtherUtilities.bubbleSort(allFoldersInAFolder, allFoldersInAFolderIndex);
		
		// now create list of all files in aFolder
		List<String> allFilesInAFolder = new ArrayList<>();
		for (AFileOrAFolder aFileOrAFolder: aFolder.getFileOrFolderList()) {
			if (aFileOrAFolder instanceof AFile) {
				allFilesInAFolder.add(aFileOrAFolder.getName());
			}
		}
		
		// sort alphabetically
		Collections.sort(allFilesInAFolder, (a,b) -> b.compareTo(a));
		
		// now add the files to the existing list of folders
		// now we have a list with first the folders, alphabetically sorted, then the files, alphabetically sorted
		allFoldersInAFolder.addAll(allFilesInAFolder);
		
		// and now also add ".." if parentFolder is not null, so user can go back a level
		if (parentFolders.size() != 0) {
			allFoldersInAFolder.add(0, "..");
			allFoldersInAFolderIndex.add(0, 0);// arbitrary value
		}
		
		// create the list to show in the popup
		ListView<String> listView = new ListView<>(FXCollections.observableArrayList(allFoldersInAFolder));
		
		// User needs to see the difference between files and folders - files will be not selectable (ie disabled)
		setIconAndSelectable(listView, amountOfFoldersInAFolder);
		
		// Handle selection of an item
		listView.setOnMouseClicked(e -> {
            
			// if it's not a folder then don't react
			if (listView.getSelectionModel().getSelectedIndex() >= amountOfFoldersInAFolder) {return;}
					
			boolean digDeeper = false;
			
			// if double click then we'll dig deeper into the selected folder, meaning show all folders in the selected folder
			// but the currently selected folder will be the same,  no matter if it's a single click or a double click
			if (e.getClickCount() == 2) {digDeeper = true;}
			
			int selectedIndex = listView.getSelectionModel().getSelectedIndex();

	        // will be the folder that will be used next time we create the button, only if digDeeper = true
	        AFolder nextFolder = null;
	        
	        //if parentFolders.size() != 0, means there's a parent, and the first element is ".."
	        //if user clicked the first element, then user actually wants to go a level up
	        // only if double click is chosen
	        if (e.getClickCount() == 2) {
		        if (parentFolders.size() != 0 && selectedIndex == 0) {
		        	currentlySelectFolderToRestoreAString = OtherUtilities.getParentFolder(currentlySelectFolderToRestoreAString);
		        	
		        	// remove last element from parentFolders, and assign it to nextFolder
		        	// only if digDeeper = true
		        	if (digDeeper) {nextFolder = parentFolders.removeLast();}

		        } else {
		            currentlySelectFolderToRestoreAString = 
		            		currentlySelectFolderToRestoreAString + 
		            		OtherUtilities.getSeperatorToAdd(currentlySelectFolderToRestoreAString) + 
		            		allFoldersInAFolder.get(selectedIndex); 
		            
		            // add aFolder to parentFolders
		        	// only if digDeeper = true
		            if (digDeeper) {parentFolders.add(aFolder);}
		            
		            nextFolder = (AFolder)aFolder.getFileOrFolderList().get(allFoldersInAFolderIndex.get(selectedIndex));
		            
		        }
	        }
	        
            // set the selected folder to restore in the label
            selectFolderToRestoreLabel.setText(currentlySelectFolderToRestoreAString);
            
            // also set in uiparam 
            UIParameters.getInstance().setFolderToRestore(currentlySelectFolderToRestoreAString);
            
            //// if digDeeper = true
            // attach a new popup with contents of the selected folder
            // the folder is now the just selected folder, the index of that folder is in allFoldersInAFolderIndex
            // calling this function will actually result in
            // - creation of a new listview with new folder contents
            // - creation and showing a new popup with the contents, and adding it to selectFolderToRestorePopup
            // - so it actually refreshes selectFolderToRestorePopup
            // we hide the pop up here, it will be reopened
            if (digDeeper) {
            	selectFolderToRestorePopup.hide();
            	attachPopUpToSelectFolderToRestoreButton(nextFolder);
            	selectFolderToRestorePopup.show(stage);
            }
            
            
        });
		
		selectFolderToRestoreButton.setOnAction(e -> selectFolderToRestorePopup.show(stage));
		
		if (amountOfFoldersInAFolder > 0) {
			selectFolderToRestoreButton.setDisable(false);
			/*if (!allFoldersInAFolder.contains(currentlySelectFolderToRestoreAString.getText())) {
				selectedBackupLabel.setText(defaultBackupFolderTextString);
				if (backupFolderSelectedHandler != null) {
	            	backupFolderSelectedHandler.handleSelectedBackupFolder(defaultBackupFolderTextString);
	            }
			}*/
			
		} else {
			selectFolderToRestoreButton.setDisable(true);
			/*selectedBackupLabel.setText(defaultBackupFolderTextString);
			if (backupFolderSelectedHandler != null) {
            	backupFolderSelectedHandler.handleSelectedBackupFolder(defaultBackupFolderTextString);
            }*/
		}
		
        String infoSelectedFolderInfoLabelsString = "Huidige folder:\n"
        		+ currentlySelectFolderToRestoreAString; 
        Label infoSelectedFolderInfoLabel = new Label();
        infoSelectedFolderInfoLabel.setText(infoSelectedFolderInfoLabelsString);

        // Create a Pane to contain the cancel button
        Pane infoLabelContainer = new Pane(infoSelectedFolderInfoLabel);
        infoLabelContainer.setMinWidth(selectFolterToRestorePopupWidth); 
        infoLabelContainer.setStyle("-fx-background-color: white; -fx-border-color: #0077CC; -fx-border-width: 2px;");

        // Create a layout for the popup content
        VBox popupContent = new VBox(listView, infoLabelContainer, UIUtilities.createCancelButtonContainer(selectFolderToRestorePopup, selectFolterToRestorePopupWidth, "Ok"));

        popupContent.setSpacing(5);
        popupContent.setPrefWidth(selectFolterToRestorePopupWidth);
        
        selectFolderToRestorePopup.getContent().removeAll(popupContent);
        selectFolderToRestorePopup.getContent().addAll(popupContent);
		
	}
	
	/**
	 * listview consists first a list of folders, then a list of files. It needs to be visible to the user what are the files and what are the folders<br>
	 * This function gives an different icons for folders and files and also sets files not selectable
	 * 
	 * @param listView
	 * @param amountOfFolders
	 */
	private static void setIconAndSelectable(ListView<String> listView, int amountOfFolders) {
		
		listView.setCellFactory(param -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                	
                    // Set text
                    setText(item);

                    // set icon for file or folder
                    ImageView icon = new ImageView();
                    if (getIndex() >= amountOfFolders) {
                        setTextFill(Color.GREY); // Change color for specific items
                        icon.setImage(new Image(getClass().getResourceAsStream("/img/file-1453.png")));
                        setDisable(true);
                    } else {
                        icon.setImage(new Image(getClass().getResourceAsStream("/img/folder-1484.png")));
                    }
                    setGraphic(icon);
                }
            }
        });

	}

}
