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
package main;

import java.io.File;
import java.util.Date;
import java.util.regex.Pattern;

import Enumerations.Action;
import Interfaces.FolderChangedHolder;
import Interfaces.ProcessText;
import Interfaces.TextFieldChanged;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import model.CommandLineArguments;
import model.UIParameters;
import pcbackup.Backup;
import pcbackup.Restore;
import utilities.OtherUtilities;

public class Main extends Application {

	// screen parameters
    private int sceneWidth = 800;
    private int sceneHeight = 400;
    private int spacingBetweenSections = 10;

	// Attributes for root VBox
    private VBox root = new VBox();

	// Attributes for Section with additional backup parameters (like excluded file list)
    private VBox sectionBackupParametersBox;
    
    // Attributes for Section with additional restore parameters
    private VBox sectionRestoreParametersBox;

    // Attributes for Section with submit button
    private VBox submitButtonVBox;
    private Button submitButton;
    
    private static Stage primaryStage;
	
    private UIParameters uiparam = UIParameters.getInstance();
    
    private ProcessText processText;
    
    /**
     * destFolderChangedHolder is used to trigger change of dest folder, dest folder is the folder with all backups in
     */
    private FolderChangedHolder destFolderChangedHolder = new FolderChangedHolder();
    
    @Override
    public void start(Stage primaryStage) {
    	
    	Main.primaryStage = primaryStage;
    	
    	processText = (logtext) -> {utilities.Logger.log(logtext);};
    	
    	// Create a VBox to hold all sections
        root.setPadding(new Insets(10));
        root.setSpacing(spacingBetweenSections);

        // Create the first section, this is the section where source folder, backup folder, log file folder are chosen
        VBox section1 = Section1.createSection1(primaryStage, (text) -> sourceTextFieldChanged(text), (text) -> destTextFieldChanged(text), (text) -> logFileFolderTextFieldChanged(text), uiparam.getSourceTextFieldTextString(), uiparam.getDestTextFieldTextString(), uiparam.getLogfileFolderTextFieldString());
        root.getChildren().add(section1);

        // Create a divider line between sections
        root.getChildren().add(createDivider());

        // section2 is where user selects the action : full backup, incremental backup, restore or search
        VBox section2 = Section2.createSection2(primaryStage, (action) -> addAndRemoveSection(action) );
        root.getChildren().add(section2);
        
        // section backupParameters - don't add it yet to the root, it will be shown only when user selects Full or Incremental backup
        sectionBackupParametersBox = SectionBackupParameters.createSectionBackupParameters(primaryStage, (text) -> excludedFileListTextFieldChanged(text), (text) -> excludePathListTextFieldChanged(text), (text) -> folderNameMappingListTextFieldChanged(text), uiparam.getExcludedFileListTextFieldTextString(), uiparam.getExcludedPathListTextFieldTextString(), uiparam.getFolderNameMappingTextFieldTextString());
        
        // add the submit button, initially disabled
        addSubmitButtonVBox(false);

        // Create a scene with the VBox and set it on the primary stage
        Scene scene = new Scene(root, sceneWidth, sceneHeight);
        primaryStage.setScene(scene);

        // Show the primary stage
        primaryStage.show();
        
    }

    private void startPCBackup() {
    	
    	if (uiparam.getCurrentlySelectedAction() == null) {
    		System.out.println("in startPCBackup, but currentlySelectedAction == null, looks like a coding error");
    	}
    	
    	switch (uiparam.getCurrentlySelectedAction()) {
    	case FULLBACKUP:
    	case INCREMENTALBACKUP:
    		
    		CommandLineArguments commandLineArgumentsForBackup = new CommandLineArguments(
    				null, null, uiparam.getSourceTextFieldTextString(), uiparam.getDestTextFieldTextString(), null, uiparam.getCurrentlySelectedAction() == Action.FULLBACKUP ? true:false, 
    						true, false, uiparam.getLogfileFolderTextFieldString(), uiparam.getExcludedFileListTextFieldTextString(), 
    						uiparam.getExcludedPathListTextFieldTextString(), null, null, uiparam.getFolderNameMappingTextFieldTextString(), false, null, null, 
    						false, false, null, processText);
    	
    		Backup.backup(commandLineArgumentsForBackup);
    		
    		break;
    	case RESTORE:

    		// create list of parameters for constructor CommandLineArguments
    		// just for clarity because the number of arguments is so long
    		Date startSearchDate = null;
    		Date endSearchDate = null;
    		String source = uiparam.getSourceTextFieldTextString();
    		String destination = uiparam.getDestTextFieldTextString();
			String restoreto = uiparam.getRestoreToFolderName();
			boolean fullBackup = false;
			boolean backup = false;
			boolean search = false;
			String logfilefolder = uiparam.getLogfileFolderTextFieldString();
			String excludedFiles = uiparam.getExcludedFileListTextFieldTextString();
			String excludedPaths = uiparam.getExcludedPathListTextFieldTextString();
			Date restoreDate = uiparam.getBackupFolderName().equalsIgnoreCase(SectionRestoreParameters.defaultBackupFolderTextString) ? new Date():OtherUtilities.getBackupDate(uiparam.getBackupFolderName(), processText);
			String folderToRestore = uiparam.getFolderToRestore();
			String folderNameMapping = uiparam.getFolderNameMappingTextFieldTextString();
			boolean overwrite = true;
			String writesearchto = null;
			Pattern searchTextPattern = null;
			boolean addpathlengthforallfolders = false;
			boolean addpathlengthforfolderswithnewormodifiedcontent = false;
			String searchText = null;
    		
    		CommandLineArguments commandLineArguments = new CommandLineArguments(startSearchDate, endSearchDate, source, destination, restoreto, fullBackup, backup, search, logfilefolder, excludedFiles, excludedPaths, restoreDate, folderToRestore, folderNameMapping, overwrite, writesearchto, searchTextPattern, addpathlengthforallfolders, addpathlengthforfolderswithnewormodifiedcontent, searchText, processText);
			
    		Restore.restore(commandLineArguments);
    		
    		break;
    	default:
    		break;
    	} 
    }
    
    /**
     * function that adds and or removes sections, depending on value of action
     * @param action
     */
    private void addAndRemoveSection(Action action) {
    	
    	uiparam.setCurrentlySelectedAction(action);
    	
    	if (action == null) {
    		root.getChildren().remove(sectionBackupParametersBox);
    		root.getChildren().remove(sectionRestoreParametersBox);
    		uiparam.setCurrentlySelectedAction(null);
    		verifySubmitButtonStatus();
    		return;
    	}
    	
    	// backup parameters box
    	if (action == Action.FULLBACKUP || action == Action.INCREMENTALBACKUP) {
    		
    		// remove any other boxes that might be there, but need to be removed because other option is chosen
    		root.getChildren().remove(sectionRestoreParametersBox);
    		
    		// add sectionBackupParametersBox if it's currently not present
    		if (!root.getChildren().contains(sectionBackupParametersBox)) {
    			boolean enabled = removeSubmitButtonVBox();
        		root.getChildren().add(sectionBackupParametersBox);
        		addSubmitButtonVBox(enabled);
    		}
    		
    	} else {
    		
    		// remove any other boxes that might be there, but need to be removed because other option is chosen
    		root.getChildren().remove(sectionBackupParametersBox);
    		
    		// add sectionBackupParametersBox if it's currently not present
    		if (!root.getChildren().contains(sectionRestoreParametersBox)) {
    			boolean enabled = removeSubmitButtonVBox();
    			if (sectionRestoreParametersBox == null) {
    	    		TextFieldChanged restoreToFolderChanged = (text) -> restoreToFolderTextFieldChanged(text);
    	    		String initialTextRestoreToFolder = "";
                    sectionRestoreParametersBox = SectionRestoreParameters.createSectionRestoreParameters(primaryStage, processText, restoreToFolderChanged, initialTextRestoreToFolder, destFolderChangedHolder, (String selectedBackupFolder) -> handleSelectedBackupFolder(selectedBackupFolder));
                    // set destFolder changed so that list of backup folders can be fetched
                    destFolderChangedHolder.folderChanged.handleNewFolder(uiparam.getDestTextFieldTextString());
        		}
        		root.getChildren().add(sectionRestoreParametersBox);
        		addSubmitButtonVBox(enabled);
    		}
    		
    	}
    	
    	verifySubmitButtonStatus();
    	
    }
    
    /**
     * removes the button if it exists, and returns if it was enabled or not before removing
     * @return
     */
    private boolean removeSubmitButtonVBox() {
    	if (submitButtonVBox == null) {return false;}
    	int submitButtonIndex = root.getChildren().indexOf(submitButtonVBox);
    	if (submitButtonIndex >= 0) {
    		boolean enabled = !submitButton.isDisabled();
    		root.getChildren().remove(submitButtonIndex);
    		return enabled;
    	}
    	return false;
    }
    
    private void addSubmitButtonVBox(boolean enabled) {
    	
    	// if it's null, then it's not yet initialized, initialize it
    	if (submitButtonVBox == null) {initializeSubmitButtonVBox(enabled);}
    	
    	// if index < 0 , then it exists, but it's not yet added
    	int submitButtonIndex = root.getChildren().indexOf(submitButtonVBox);
    	if (submitButtonIndex >= 0) {
    		submitButton.setDisable(!enabled);
    		return;
    	}
    	
    	// add it
    	root.getChildren().add(submitButtonVBox);
    	
    }
    
    private Line createDivider() {
        Line divider = new Line();
        divider.setStartX(0);
        divider.setStartY(0);
        divider.setEndX(sceneWidth - 20); // Adjust the length as needed
        divider.setEndY(0);
        divider.setStrokeWidth(1); // Adjust the thickness as needed
        divider.setStyle("-fx-stroke: black;"); // Adjust the color as needed
        return divider;
    }

    private void initializeSubmitButtonVBox(boolean enabled) {
    	
        // Add the submit button
        submitButton = new Button("Start");
        submitButton.setDisable(!enabled);
        submitButton.setOnAction(e -> startPCBackup());
        
        // Create an HBox to hold the button
        HBox submitButtonHBox = new HBox(submitButton);
        submitButtonHBox.setAlignment(Pos.BOTTOM_RIGHT); // Align the button to the right
        
        // Create the submitButtonVBox to hold the HBox and add padding
        submitButtonVBox = new VBox();
        submitButtonVBox.setSpacing(spacingBetweenSections);
        submitButtonVBox.getChildren().add(createDivider());
        submitButtonVBox.getChildren().add(submitButtonHBox);

    }
    
    private void sourceTextFieldChanged(String text) {
    	uiparam.setSourceTextFieldTextString(verifyIfFolderExists(text, (String textToProcess) -> Section1.addSourceWarning(textToProcess)));
    	verifySubmitButtonStatus();
    }
    
    private void destTextFieldChanged(String text) {
    	uiparam.setDestTextFieldTextString(verifyIfFolderExists(text, (String textToProcess) -> Section1.addDestWarning(textToProcess)));
    	destFolderChangedHolder.folderChanged.handleNewFolder(uiparam.getDestTextFieldTextString());
    	verifySubmitButtonStatus();
    }
    
    private void logFileFolderTextFieldChanged(String text) {
    	uiparam.setLogfileFolderTextFieldString(verifyIfFolderExists(text, (String textToProcess) -> Section1.addLogFolderWarning(textToProcess)));
    	verifySubmitButtonStatus();
    }
    
    private void excludedFileListTextFieldChanged(String text) {
    	uiparam.setExcludedFileListTextFieldTextString(verifyIfFileExists(text, (String textToProcess) -> SectionBackupParameters.addExcludedFileListWarning(textToProcess)));
    	verifySubmitButtonStatus();
    }
    
    private void excludePathListTextFieldChanged(String text) {
    	uiparam.setExcludedPathListTextFieldTextString(verifyIfFileExists(text, (String textToProcess) -> SectionBackupParameters.addExcludedPathListWarning(textToProcess)));
    	verifySubmitButtonStatus();
    }

    private void folderNameMappingListTextFieldChanged(String text) {
    	uiparam.setFolderNameMappingTextFieldTextString(verifyIfFileExists(text, (String textToProcess) -> SectionBackupParameters.addFolerNameMappingListWarning(textToProcess)));
    	verifySubmitButtonStatus();
    }

    private void restoreToFolderTextFieldChanged(String text) {
    	String resultString = verifyIfFolderExists(text, (String textToProcess) -> SectionRestoreParameters.addRestoreToFolderWarning(textToProcess));
    	uiparam.setRestoreToFolderName(resultString);
    	
    	// here specific handling different than other folders
    	// if folder is the same as the source folder, then give green text, info, .. to explain to take care if same folder is used as the source
    	if (resultString != null && resultString.length() > 0 && uiparam.getSourceTextFieldTextString().length() > 0 && resultString.equalsIgnoreCase(uiparam.getSourceTextFieldTextString())) {
    		SectionRestoreParameters.addRestoreToFolderInfo("Je hebt de oorspronkelijke map gekozen als doelmap voor herstel.\n"
    				+ "De oorspronkelijke mappen en bestanden zullen overschreven worden.\n"
    				+ "Mogelijks zitten er in de oorspronkelijke map, mappen en bestanden die niet in de backup zitten die je geselecteerd hebt.\n"
    				+ "Deze zullen dus ook na het herstel niet verwijderd zijn.\n"
    				+ "Hou hier rekening mee, en maak eventueel de oorspronkelijke map (of de submap in die map) leeg vooraleer het herstel te starten.");
    	}
    	
    	verifySubmitButtonStatus();
    }
    
    private void handleSelectedBackupFolder(String backup) {
        uiparam.setBackupFolderName(backup);
        verifySubmitButtonStatus();
    }

    /**
     * verifies if folderToVerify is a valid folder, and if yes calls processText with warning text and call verifySubmitButtonStatus
     * @param folderToVerify
     * @param processText
     * @return null if verification failed
     */
    private String verifyIfFolderExists(String folderToVerify, ProcessText processText) {
    	String warningTextString =  "Dit is geen geldige map";
    	
    	if (folderToVerify == null || folderToVerify.length() == 0) {
    		processText.process("");
        	return folderToVerify;
    	}
    	
    	File directory = new File(folderToVerify);
    	if (!directory.exists()) {
    		processText.process(warningTextString);
    		return null;
    	}
    	if (!directory.isDirectory()) {
    		processText.process(warningTextString);
    		return null;
    	}
    	processText.process("");
    	return folderToVerify;
    }	
    
    private String verifyIfFileExists(String fileToVerify, ProcessText processText) {
    	String fileDoesNotExistwarningTextString =  "Bestand niet gevonden.";
    	String thisIsNotAFileTextString = "Dit is geen bestand.";
    	
    	if (fileToVerify == null || fileToVerify.length() == 0) {
    		processText.process("");
        	return fileToVerify;
    	}
    	
    	File file = new File(fileToVerify);
    	if (!file.exists()) {
    		processText.process(fileDoesNotExistwarningTextString);
    		return null;
    	}
    	if (!file.isFile()) {
    		processText.process(thisIsNotAFileTextString);
    		return null;
    	}
    	processText.process("");
    	return fileToVerify;
    }	
    
    private void verifySubmitButtonStatus() {
    	
    	if (uiparam.getCurrentlySelectedAction() == null) {
    		submitButton.setDisable(true);
    		return;
    	}
    	
    	switch (uiparam.getCurrentlySelectedAction()) {
    	case INCREMENTALBACKUP:
    	case FULLBACKUP:
    		if (uiparam.getSourceTextFieldTextString() != null && uiparam.getDestTextFieldTextString() != null && uiparam.getLogfileFolderTextFieldString() != null) {
    			if (uiparam.getSourceTextFieldTextString().length() > 0 && uiparam.getDestTextFieldTextString().length() > 0 && uiparam.getLogfileFolderTextFieldString().length() > 0) {
    				submitButton.setDisable(false);
    			} else {
    				submitButton.setDisable(true);
    				break;
    			}
    		} else {
    			submitButton.setDisable(true);
    			break;
    		}
    		
    		break;
    		
    	case RESTORE:
    		if (uiparam.getSourceTextFieldTextString() != null && uiparam.getDestTextFieldTextString() != null && uiparam.getLogfileFolderTextFieldString() != null) {
    			if (uiparam.getSourceTextFieldTextString().length() > 0 && uiparam.getDestTextFieldTextString().length() > 0 && uiparam.getLogfileFolderTextFieldString().length() > 0) {
    				
    				if (uiparam.getRestoreToFolderName() != null && uiparam.getRestoreToFolderName().length() > 0) {
    					
    					if (!uiparam.getBackupFolderName().equalsIgnoreCase(SectionRestoreParameters.defaultBackupFolderTextString)) {
    						
    						submitButton.setDisable(false);
    						
    					} else {
        					submitButton.setDisable(true);
        					break;
        				} 
    					
    				} else {
    					submitButton.setDisable(true);
    					break;
    				}
    				
    			} else {
    				submitButton.setDisable(true);
    				break;
    			}
    		} else {
    			submitButton.setDisable(true);
    			break;
    		}
    		
    	default:
    		break;
    	}
    	
    }
    


    public static void main(String[] args) {
        launch(args);
    }
}
