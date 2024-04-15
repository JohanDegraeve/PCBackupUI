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

import Enumerations.Action;
import Interfaces.ProcessText;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class Main extends Application {

	// screen parameters
    private int sceneWidth = 800;
    private int sceneHeight = 400;
    private int spacingBetweenSections = 10;

	// Attributes for root VBox
    private VBox root = new VBox();

    // Attributes for Section with source, dest and logfile folder 
    private String sourceTextFieldTextString;
    private String destTextFieldTextString;
    private String logfileFolderTextFieldString;
    private String excludedFileListTextFieldTextString;
    private String excludedPathListTextFieldTextString;
    private String folderNameMappingTextFieldTextString;

	// Attributes for Section with additional backup parameters (like excluded file list)
    private VBox sectionBackupParametersBox;

    // Attributes for Section with submit button
    private VBox submitButtonVBox;
    private Button submitButton;
	
    private Action currentlySelectedAction = null;

    @Override
    public void start(Stage primaryStage) {
    	
    	// Create a VBox to hold all sections
        root.setPadding(new Insets(10));
        root.setSpacing(spacingBetweenSections);

        // Create the first section
        VBox section1 = Section1.createSection1(primaryStage, (text) -> sourceTextFieldChanged(text), (text) -> destTextFieldChanged(text), (text) -> logFileFolderTextFieldChanged(text));
        root.getChildren().add(section1);

        // Create a divider line between sections
        root.getChildren().add(createDivider());

        // section2 is where user selects the action : full backup, incremental backup, restore or search
        VBox section2 = Section2.createSection2(primaryStage, (action) -> addAndRemoveSection(action) );
        root.getChildren().add(section2);
        
        // section backupParameters
        sectionBackupParametersBox = SectionBackupParameters.createSectionBackupParameters(primaryStage, (text) -> excludedFileListTextFieldChanged(text), (text) -> excludePathListTextFieldChanged(text), (text) -> folderNameMappingListTextFieldChanged(text));
        
        // add the submit button, initially disabled
        addSubmitButtonVBox(false);

        // Create a scene with the VBox and set it on the primary stage
        Scene scene = new Scene(root, sceneWidth, sceneHeight);
        primaryStage.setScene(scene);

        // Show the primary stage
        primaryStage.show();
        
    }

    private void startBackup() {
        
    }
    
    /**
     * function that adds and or removes sections, depending on value of action
     * @param action
     */
    private void addAndRemoveSection(Action action) {
    	
    	currentlySelectedAction = action;
    	
    	if (action == null) {
    		root.getChildren().remove(sectionBackupParametersBox);
    		currentlySelectedAction = null;
    		verifySubmitButtonStatus();
    		return;
    	}
    	
    	// backup parameters box
    	if (action == Action.FULLBACKUP || action == Action.INCREMENTALBACKUP) {
    		if (!root.getChildren().contains(sectionBackupParametersBox)) {
    			boolean enabled = removeSubmitButtonVBox();
        		root.getChildren().add(sectionBackupParametersBox);
        		addSubmitButtonVBox(enabled);
    		}
    	} else {
    		root.getChildren().remove(sectionBackupParametersBox);
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
        submitButton.setOnAction(e -> startBackup());
        
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
    	sourceTextFieldTextString = verifyIfFolderExistsAndSetSubmitButton(text, (String textToProcess) -> Section1.addSourceWarning(textToProcess));
    	verifySubmitButtonStatus();
    }
    
    private void destTextFieldChanged(String text) {
    	destTextFieldTextString = verifyIfFolderExistsAndSetSubmitButton(text, (String textToProcess) -> Section1.addDestWarning(textToProcess));
    	verifySubmitButtonStatus();
    }
    
    private void logFileFolderTextFieldChanged(String text) {
    	logfileFolderTextFieldString = verifyIfFolderExistsAndSetSubmitButton(text, (String textToProcess) -> Section1.addLogFolderWarning(textToProcess));
    	verifySubmitButtonStatus();
    }
    
    private void excludedFileListTextFieldChanged(String text) {
    	excludedFileListTextFieldTextString = verifyIfFileExistsAndSetSubmitButton(text, (String textToProcess) -> SectionBackupParameters.addExcludedFileListWarning(textToProcess));
    	verifySubmitButtonStatus();
    }
    
    private void excludePathListTextFieldChanged(String text) {
    	excludedPathListTextFieldTextString = verifyIfFileExistsAndSetSubmitButton(text, (String textToProcess) -> SectionBackupParameters.addExcludedPathListWarning(textToProcess));
    	verifySubmitButtonStatus();
    }

    private void folderNameMappingListTextFieldChanged(String text) {
    	folderNameMappingTextFieldTextString = verifyIfFileExistsAndSetSubmitButton(text, (String textToProcess) -> SectionBackupParameters.addFolerNameMappingListWarning(textToProcess));
    	verifySubmitButtonStatus();
    }

    /**
     * verifies if folderToVerify is a valid folder, and if yes calls processText with warning text and call verifySubmitButtonStatus
     * @param folderToVerify
     * @param processText
     * @return null if verification failed
     */
    private String verifyIfFolderExistsAndSetSubmitButton(String folderToVerify, ProcessText processText) {
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
    
    private String verifyIfFileExistsAndSetSubmitButton(String fileToVerify, ProcessText processText) {
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
    	
    	if (currentlySelectedAction == null) {
    		submitButton.setDisable(true);
    		return;
    	}
    	
    	switch (currentlySelectedAction) {
    	case FULLBACKUP, INCREMENTALBACKUP:
    		if (sourceTextFieldTextString != null && destTextFieldTextString != null && logfileFolderTextFieldString != null) {
    			if (sourceTextFieldTextString.length() > 0 && destTextFieldTextString.length() > 0 && logfileFolderTextFieldString.length() > 0) {
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
    	default:
    		break;
    	}
    	
    }
    


    public static void main(String[] args) {
        launch(args);
    }
}
