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
import java.nio.file.Paths;
import java.util.Date;
import java.util.regex.Pattern;

import Enumerations.Action;
import Interfaces.FolderChangedHolder;
import Interfaces.ProcessText;
import Interfaces.TextFieldChanged;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.CommandLineArguments;
import model.UIParameters;
import pcbackup.Backup;
import pcbackup.Restore;
import utilities.OtherUtilities;
import utilities.PathUtilities;

public class Main extends Application {

	private String VERSION_STRING = "1.1.1";
	
	/**
	 * info written in logfile at start up
	 */
	private String STARTUP_INFO = "PCBackupUI versie - " + VERSION_STRING + " - Een applicatie voor het maken van back-ups.\n" +
			"Copyright © 2024 - Johan Degraeve - Alle rechten voorbehouden.\n" +
			"Licentie: GNU GPL 3.0. Code beschikbaar op GitHub: https://github.com/JohanDegraeve/PCBackupUI\n";
	
	private String restoreWarningTextStringTemplate = "Je hebt gekozen om mappen en/of bestanden te herstellen met als doelmap \n"
			+ "{doelmap}\n"
			+ "Deze map is echter niet leeg.\n"
			+ "\n"
			+ "Als je nu een herstel doet riskeer je inconsistentie. \n"
			+ "Mogelijke scenarios waarbij je inconsistentie creëert:\n"
			+ "\n"
			+ "- Je hebt tussen het herstelpunt (= datum en tijd van de gekozen backup) en nu een submap (= map binnen de doelmap) een andere naam gegeven.\n"
			+ "Na het herstel zul je de twee submappen vinden in de doelmap (naast mogelijks nog andere submappen), één keer met de oude naam, één keer met de nieuwe naam. \n"
			+ "De map met de oude naam bevat de bestanden zoals ze waren op het herstelpunt, de map met de nieuwe naam bevat de huidige versie.\n"
			+ "\n"
			+ "- Je hebt tussen het herstelpunt en nu een bestand een andere naam gegeven.\n"
			+ "Na het herstel zul je de twee bestanden vinden, één keer met de oude naam, één keer met de nieuwe naam. \n"
			+ "Het bestand met de oude naam is het bestand op het moment van het  herstelpunt, het bestand met de nieuwe naam is de huidige versie.\n"
			+ "\n"
			+ "- Mogelijks heb je een bestand of een submap verplaatst naar een submap binnen de doelmap.\n"
			+ "Na het herstel zul je weer de oude map en de huidige map terugvinden, de eerste op de originele plaats, de tweede op de huidige plaats.\n"
			+ "\n"
			+ "- Hou er ook rekening mee dat bestanden overschreven worden. Dus bestanden die nu bestaan in de doelmap (of submappen van de doelmap) en die reeds een versie hadden"
			+ "op het herstelpunt, op dezelfde locatie (dezelfde submap) zullen de huidige versie overschrijven.\n"
			+ "\n"
			+ "Denk dus goed na en \n"
			+ "- verander map waar de herstelde mappen moeten komen: Klik op Annuleer, en kies dan bij 'Selecteer folder waar de herstelde mappen moeten komen' een andere map.\n"
			+ "of\n"
			+ "- maak de doelmap leeg vooraleer verder te gaan (de map is {doelmap}) en klik daarna op 'Start Herstel'\n"
			+ "of\n"
			+ "- doe gewoon verder (klik op 'Start Herstel') en hou rekening met mogelijk inconsistenties na het herstel.\n\n";
	
	private String additionalRestoreWarningTextString = "NOG EEN BELANGRIJKE TIP:\n"
			+ "Je gekozen doelmap is ook de map waar de oorspronkelijke bestanden zich bevinden.\n"
			+ "Dit is Ok. Maar als die map nu ook de plaats is waar je OneDrive (of SharePoint) bestanden gesynchroniseerd worden "
			+ "en je bent van plan de doelmap eerst leeg te maken vooraleer het herstel te starten, sluit dan eerst even je Onedrive applicatie af.\n"
			+ " ==> Klik op het OneDrive icon onderaan rechts, klik op het Instellingen tandwiel, en kies 'Onedrive Afsluiten'. "
			+ "Verwijder daarna pas (eventueel) de bestaande bestanden en mappen, en start daarna pas het herstel. Zodra het herstel is uitgevoerd, kun je OneDrive terug opstarten.";

	
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
    private Button helpButton;
    
    private static Stage primaryStage;
	
    private UIParameters uiparam = UIParameters.getInstance();
    
    // to process logging information
    private ProcessText processText;
    
    // for status logging
    private TextArea loggingTextArea = new TextArea();
    private Stage statusStage;

    
    /**
     * destFolderChangedHolder is used to trigger change of dest folder, dest folder is the folder with all backups in
     */
    private FolderChangedHolder destFolderChangedHolder = new FolderChangedHolder();
    
    @SuppressWarnings("exports")
	@Override
    public void start(Stage primaryStage) {
    	
    	Main.primaryStage = primaryStage;
    	
    	processText = (logtext) -> {
    		
    		// w
    		utilities.Logger.log(logtext);
    		
	        Platform.runLater(() -> {
	        	loggingTextArea.appendText(logtext + "\n");
	        });
    	 
    		
    	};
    	
    	processText.process(STARTUP_INFO);
    	
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
        
        primaryStage.setTitle("PCBackupUI - versie " + VERSION_STRING);

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

    		openStatusWindow();

    		Thread thread = new Thread(new Backup(commandLineArgumentsForBackup));
            thread.start();
                        
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
    		
    		CommandLineArguments commandLineArgumentsForRestore = new CommandLineArguments(startSearchDate, endSearchDate, source, destination, restoreto, fullBackup, backup, search, logfilefolder, excludedFiles, excludedPaths, restoreDate, folderToRestore, folderNameMapping, overwrite, writesearchto, searchTextPattern, addpathlengthforallfolders, addpathlengthforfolderswithnewormodifiedcontent, searchText, processText);

    		// variable needed in warning checks
			File directory = new File(Paths.get(uiparam.getRestoreToFolderName()).resolve(PathUtilities.applyFolderNameMappingReversed(uiparam.getFolderToRestore(), commandLineArgumentsForRestore)).toString());

    		// check if restoreto folder is a subfolder of the sourcefolder and if yes 
    		//   then check if restoreto folder + foldertorestore exists and if it contains files
    		// if so give warning
			// if it's not a subfolder of source, then just check if it has files and if yes then other warning must be shown
			if (directory.exists()) {
				File[] files = directory.listFiles();
				if (files.length > 0) {	
					
					Boolean listHasNotExcludedFiles = false;
					for (File file: files) {
						if (!commandLineArgumentsForRestore.excludedFiles.contains(file.getName())) {
							listHasNotExcludedFiles = true;
							break;
						}
					}
					
					if (listHasNotExcludedFiles) {
						String warningTextString = restoreWarningTextStringTemplate.replaceAll("\\{doelmap\\}", directory.getPath());
						
						if (uiparam.getRestoreToFolderName().startsWith(uiparam.getSourceTextFieldTextString())) {
							warningTextString = warningTextString + additionalRestoreWarningTextString;
	                    }
						
	    				showRestoreWarning(warningTextString, commandLineArgumentsForRestore, processText);
	    				return;
		    				
					}
					
				}
			}
			
    		
    		startRestore(commandLineArgumentsForRestore);
    		
    		break;
    	default:
    		break;
    	} 
    }
    
    /**
     * shows warning that user has selected the source (or a subfolder of the source) as a restoretofolder 
     */
    private void showRestoreWarning(String warningTextString, CommandLineArguments commandLineArguments, ProcessText processText) {
    	
        // new stage for the popup
        Stage popupStage = new Stage();
        
        // the pop up should block the main application
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Let op!");
        
        VBox popupContent = new VBox();
        popupContent.setSpacing(10);
        popupContent.setStyle("-fx-padding: 10; -fx-background-color: white; -fx-alignment: center;");
        popupContent.setMaxWidth(sceneWidth - 20);

     // Maak een ScrollPane en voeg de VBox toe aan de ScrollPane
        ScrollPane scrollPane = new ScrollPane(popupContent);
        scrollPane.setFitToWidth(false);
        scrollPane.setFitToHeight(true);
        
        // cancel button
        Button cancelButton = new Button("Annuleer");
        cancelButton.setOnAction(e -> { 
        		processText.process("Restore geannulleerd door gebruiker");
        		popupStage.close();
        }
        );
        
        // ok button
        Button okButton = new Button("Start Herstel");
        okButton.setOnAction(e -> { 
        		startRestore(commandLineArguments); 
        		popupStage.close();
        	}
        );
        
        // create hbox that will hold the two buttons
        HBox buttonHBox = new HBox(10);
        buttonHBox.setPadding(new Insets(10)); // margin on both sides
        buttonHBox.getChildren().addAll(okButton, cancelButton);
        
        Text warningText = new Text(warningTextString);
        warningText.setWrappingWidth(sceneWidth - 40);
        popupContent.getChildren().addAll(warningText, buttonHBox);
        
        // Maak een Scene voor de pop-up
        Scene popupScene = new Scene(scrollPane, sceneWidth, sceneHeight);
        
        // to make sure that by default the scrollbar is on top
        popupStage.setOnShown(event -> Platform.runLater(() -> scrollPane.setVvalue(0)));  // Scroll naar boven

        // Stel de Scene in op de pop-up Stage
        popupStage.setScene(popupScene);
        
        // Toon de pop-up
        popupStage.showAndWait();
        
    }

    private void startRestore(CommandLineArguments commandLineArguments) {
		
		openStatusWindow();
		
		Restore.restore(commandLineArguments);
		
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
    		
    		// add sectionRestoreParametersBox if it's currently not present
    		if (!root.getChildren().contains(sectionRestoreParametersBox)) {
    			
    			// get current status of submit button, is it enabled or not?
    			
    			boolean enabled = removeSubmitButtonVBox();
    			
    			// if sectionRestoreParametersBox is null, then create it
    			if (sectionRestoreParametersBox == null) {
    	    		TextFieldChanged restoreToFolderChanged = (text) -> restoreToFolderTextFieldChanged(text);
    	    		
    	    		// initially set restoreToFolder equal to source folder
    	    		String initialTextRestoreToFolder = uiparam.getSourceTextFieldTextString();
    	    		uiparam.setRestoreToFolderName(initialTextRestoreToFolder);
    	    		
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
    	
        // create the submit button
        submitButton = new Button("Start");
        submitButton.setDisable(!enabled);
        submitButton.setOnAction(e -> startPCBackup());
        //submitButton.setAlignment(Pos.BOTTOM_RIGHT);
        
        // create the helpbutton
        helpButton = new Button("Info");
        helpButton.setOnAction(e -> showCopyrightInfo());

        // Maak een Region om de ruimte tussen de knoppen te vullen
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Create an HBox to hold the buttons and the spacer
        HBox submitButtonHBox = new HBox(10);
        submitButtonHBox.setPadding(new Insets(10)); // margin on both sides
        submitButtonHBox.getChildren().addAll(helpButton, spacer, submitButton);

        // Create the submitButtonVBox to hold the HBox and add padding
        submitButtonVBox = new VBox();
        submitButtonVBox.setSpacing(spacingBetweenSections);
        submitButtonVBox.getChildren().add(createDivider());
        submitButtonVBox.getChildren().addAll(submitButtonHBox);

    }
    
    private void showCopyrightInfo() {
    	
        // Nieuwe Stage voor de pop-up
        Stage popupStage = new Stage();
        
        // Zorg dat de pop-up de hoofdapplicatie blokkeert
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Info");
        
        // Voeg inhoud toe aan de pop-up
        VBox popupContent = new VBox();
        popupContent.setSpacing(10);
        popupContent.setStyle("-fx-padding: 10; -fx-background-color: white; -fx-alignment: center;");

        // 	Maak knoppen voor de pop-up
        Button closeButton = new Button("Sluiten");
        closeButton.setOnAction(e -> popupStage.close());
        
        Text copyrightText = new Text(STARTUP_INFO);
        popupContent.getChildren().addAll(copyrightText, closeButton);
        
        // Maak een Scene voor de pop-up
        Scene popupScene = new Scene(popupContent, sceneWidth, sceneHeight);
        
        // Stel de Scene in op de pop-up Stage
        popupStage.setScene(popupScene);
        
        // Toon de pop-up
        popupStage.showAndWait();
        
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
    	
    	verifySubmitButtonStatus();
    }
    
    /**
     * backupfolder is used for restore, this function is called when that is changed
     * @param backup
     */
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
    
    private void openStatusWindow() {
    	
    	if (statusStage != null) {
    		
    		// Check if the stage is minimized (iconified)
            if (statusStage.isIconified()) {
                // Restore the minimized window
            	statusStage.setIconified(false);
            }
            
            // bring the status to the front
    		statusStage.requestFocus();
    		
            return;
            
        }
    	
    	// statusStage is null, create it
    	
		statusStage = new Stage();
        VBox statusRoot = new VBox(10);
	
        loggingTextArea.setEditable(false); // Make the text area read-only
        loggingTextArea.setMinHeight(Region.USE_PREF_SIZE); // Set text area to expand as needed
        VBox.setVgrow(loggingTextArea, Priority.ALWAYS); 

        statusRoot.getChildren().add(loggingTextArea);
        
        // Set VBox to fill the entire scene and align content to top center
        statusRoot.setFillWidth(true);
        statusRoot.setAlignment(Pos.TOP_CENTER);
        
        statusStage.setOnCloseRequest(event -> {
        	// if user closes the window, set statusStage to null
        	statusStage = null;
        });

        Scene statusScene = new Scene(statusRoot, sceneWidth, sceneHeight);
        statusStage.setScene(statusScene);
        statusStage.setTitle("Status");
        statusStage.show();

    }



    public static void main(String[] args) {
        launch(args);
    }
}
