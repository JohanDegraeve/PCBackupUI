package main;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;


import Interfaces.FolderChangedHolder;
import Interfaces.ProcessText;
import Interfaces.TextFieldChanged;
import javafx.geometry.Pos;import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.DateCell;
import javafx.util.Callback;
import model.UIParameters;
import utilities.ListBackupsInFolder;
import utilities.OtherUtilities;
import utilities.UIUtilities;

public class SectionSearchParameters {

	static final String defaultDateString = "Geen datum geselecteerd";
	
	/// Global variables
	private static ProcessText processText;
	private static Stage stage;
	
	private static VBox completeSelectStartSearchDateVBox;
	private static HBox selectStartSearchDateHBox;
	
	private static VBox completeSelectEndSearchDateVBox;
	private static HBox selectEndSearchDateHBox;
	
	private static VBox completeSearchTextsVBox;
	private static HBox searchText1HBox;
	private static HBox searchText2HBox;
	private static HBox searchText3HBox;
	
	private static TextField textField1;
	private static TextField textField2;
	private static TextField textField3;

	private static Date minimumStartSearchDate;
	private static Date maximumStartSearchDate;

	private static VBox completeWriteSearchToFolderVBox;
	private static HBox writeSearchToFolderHBoxWithFolderText;
	private static HBox searchToFolderHBoxWithLabelHBox;
	private static Label searchToFolderWarningLabel;

	/**
	 * 
	 * @param stage
	 * @param processText
	 * @param writeSearchToFolderChanged for when user types or removes characters in the writeSearchToFolder field
     * @param destFolderChangedHolder is the folder where backup subfolders are stored, that folder is needed here because we get all backup folder names to determine the earlieast and latest search date
     * @param anySearchTextFieldChanged call back if any of the search texts fields is changed
	 * @return
	 */
	@SuppressWarnings("exports")
	public static VBox createSectionSearchParameters(Stage stage, TextFieldChanged writeSearchToFolderChanged, ProcessText processText, FolderChangedHolder destFolderChangedHolder, TextFieldChanged anySearchTextFieldChanged, String initialTextSource) {
	
		String labelSelectStartSearchDate = "Vanaf welke datum wil je zoeken in de backups:\n";
		String labelSelectStartSearchDateToolTipString = "Er zal gezocht worden in all backups die gemaakt zijn op of na deze dataum.\n" +
		"Je kunt dit veld leeglaten, dan zal er gezocht worden vanaf de eerste backup.";
		
		String labelSelectEndSearchDate = "Tot welke datum wil je zoeken in de backups:";
		String labelSelectEndSearchDateToolTipString = "Er zal gezocht worden in all backups die gemaakt zijn op of voor deze dataum.\n" +
		"Je kunt dit veld leeglaten, dan zal er gezocht worden tot en met de laatste backup.\n" + 
		"Klik om de datum aan te passen";
		
		String labelGiveSearchText1String = "Geef minstens één tekst waarnaar moet gezocht worden:\n";
		String labelGiveSearchTextToolTipString = "Er wordt naar alle bestanden gezocht  die tot maximaal 3 teksten bevatten in de naam van het bestand";
		String labelGiveSearchText2String = "Geef optioneel een tweede tekst waarnaar moet gezocht worden:\n";
		String labelGiveSearchText3String = "Geef optioneel een derde tekst waarnaar moet gezocht worden:\n";
		
		
		destFolderChangedHolder.folderChanged = (String folder) -> destFolderChangedHandler(folder);

		SectionSearchParameters.processText = processText;
		SectionSearchParameters.stage = stage;
		
		VBox section = new VBox();
        section.setSpacing(5);
        
        completeSelectStartSearchDateVBox = new VBox();
        completeSelectEndSearchDateVBox = new VBox();
        completeSearchTextsVBox = new VBox();
        completeWriteSearchToFolderVBox = new VBox();
		
		////////////////////////////////////////////////////////////
		//////         CREATE STARTSEARCHDATE VBOX           ///////
		////////////////////////////////////////////////////////////
		
		// create label which gives explanation
        Label startSearchDateLabel = new Label(labelSelectStartSearchDate);
        UIUtilities.addToolTip(startSearchDateLabel, labelSelectStartSearchDate + labelSelectStartSearchDateToolTipString);
        
        DatePicker startSearchDatePicker = new DatePicker();
        // set minimum and maximum date
        setMinimumAndMaximumDate(startSearchDatePicker, minimumStartSearchDate, maximumStartSearchDate);
        // set selectedStartSearchDate to date selected by user
        startSearchDatePicker.setOnAction(event -> {
        	
        	LocalDate selectedDateAsLocalDate = startSearchDatePicker.getValue();
        	// selectedDateAsLocalDate might be null: if next lines of code set selectedDateAsLocalDate to null, then it behaves as if the 
        	// action is triggered in, so this piece of code is called again, but this time with selectedDateAsLocalDate = null
        	if (selectedDateAsLocalDate == null) {return;}

        	Date selectedDate = convertLocalDateToDate(selectedDateAsLocalDate);
        	if (UIParameters.getInstance().getEndSearchDate() != null) {
        		if (UIParameters.getInstance().getEndSearchDate().getTime() < selectedDate.getTime()) {
        			// user selected an end search date which is before the startsearchdate
        			// just don't use it
        			startSearchDatePicker.setValue(null);
        			UIParameters.getInstance().setStartSearchDate(null);
        			anySearchTextFieldChanged.handleChange("");
        			return;
        		}
        	}
        	UIParameters.getInstance().setStartSearchDate(selectedDate);
			// there's no text changed, but the main function will call verify submitbuttonstatus, which is needed here
			anySearchTextFieldChanged.handleChange("");
        });
        // user should not be able to type a date
        startSearchDatePicker.setEditable(false);
        
        selectStartSearchDateHBox = new HBox();
		selectStartSearchDateHBox.setSpacing(10);//  space between individual nodes in the Hbox
		selectStartSearchDateHBox.setAlignment(Pos.CENTER_LEFT);
	    GridPane.setHgrow(selectStartSearchDateHBox, Priority.ALWAYS);
		selectStartSearchDateHBox.getChildren().addAll(startSearchDateLabel, startSearchDatePicker);
		completeSelectStartSearchDateVBox.getChildren().add(selectStartSearchDateHBox);
		section.getChildren().add(completeSelectStartSearchDateVBox);

		////////////////////////////////////////////////////////////
		//////         CREATE ENDSEARCHDATE VBOX           ///////
		////////////////////////////////////////////////////////////
		
		// create label which gives explanation
        Label endSearchDateLabel = new Label(labelSelectEndSearchDate);
        UIUtilities.addToolTip(endSearchDateLabel, labelSelectEndSearchDate + labelSelectEndSearchDateToolTipString);
        
        DatePicker endSearchDatePicker = new DatePicker();
        // set minimum and maximum date
        setMinimumAndMaximumDate(endSearchDatePicker, minimumStartSearchDate, maximumStartSearchDate);
        // set selectedEndSearchDate to date selected by user
        endSearchDatePicker.setOnAction(event -> {
        	
        	LocalDate selectedDateAsLocalDate = endSearchDatePicker.getValue();
        	// selectedDateAsLocalDate might be null: if next lines of code set selectedEndSearchDate to null, then it behaves as if the 
        	// action is triggered in, so this piece of code is called again, but this time with selectedDateAsLocalDate = null
        	if (selectedDateAsLocalDate == null) {return;}
        	
        	Date selectedDate = convertLocalDateToDate(selectedDateAsLocalDate);
        	if (UIParameters.getInstance().getStartSearchDate() != null) {
        		if (selectedDate.getTime() < UIParameters.getInstance().getStartSearchDate().getTime()) {
        			// user selected an start search date which is after the endsearchdate
        			// just don't use it
        			endSearchDatePicker.setValue(null);
        			UIParameters.getInstance().setEndSearchDate(null);
        			anySearchTextFieldChanged.handleChange("");
        			return;
        		}
        	}
        	UIParameters.getInstance().setEndSearchDate(selectedDate);
			// there's no text changed, but the main function will call verify submitbuttonstatus, which is needed here
			anySearchTextFieldChanged.handleChange("");
        });
        // user should not be able to type a date
        endSearchDatePicker.setEditable(false);
        
        selectEndSearchDateHBox = new HBox();
		selectEndSearchDateHBox.setSpacing(10);//  space between individual nodes in the Hbox
		selectEndSearchDateHBox.setAlignment(Pos.CENTER_LEFT);
	    GridPane.setHgrow(selectEndSearchDateHBox, Priority.ALWAYS);
		selectEndSearchDateHBox.getChildren().addAll(endSearchDateLabel, endSearchDatePicker);
		completeSelectEndSearchDateVBox.getChildren().add(selectEndSearchDateHBox);
		section.getChildren().add(completeSelectEndSearchDateVBox);

		////////////////////////////////////////////////////////////
		//////         CREATE SearchToFolder VBOX          //////
		////////////////////////////////////////////////////////////

        writeSearchToFolderHBoxWithFolderText = createHboxWithWriteSearchToFolder(stage, writeSearchToFolderChanged, initialTextSource);
        writeSearchToFolderHBoxWithFolderText.setSpacing(10);
        completeWriteSearchToFolderVBox.getChildren().add(writeSearchToFolderHBoxWithFolderText);
        section.getChildren().add(completeWriteSearchToFolderVBox);

        // intialize label and hbox that will contain the label
        searchToFolderWarningLabel = new Label();
        searchToFolderWarningLabel.setStyle("-fx-text-fill: red;");
        searchToFolderHBoxWithLabelHBox = new HBox();
        searchToFolderHBoxWithLabelHBox.getChildren().add(searchToFolderWarningLabel);

		////////////////////////////////////////////////////////////
		//////         CREATE STARTSEARCHTEXTS VBOXes           //////
		////////////////////////////////////////////////////////////
		
        /// searchText1HBox
        searchText1HBox = new HBox();
        searchText1HBox.setSpacing(10);//  space between individual nodes in the Hbox
        searchText1HBox.setAlignment(Pos.CENTER_LEFT);
        GridPane.setHgrow(searchText1HBox, Priority.ALWAYS); // allows the Hbox to grow horizontally till the end of the grid
		// create label which gives explanation
        Label searchText1Label = new Label(labelGiveSearchText1String);
        UIUtilities.addToolTip(searchText1Label, labelGiveSearchText1String + labelGiveSearchTextToolTipString);
        /// the textfield that contains the searchtext
        textField1 = new TextField();
        HBox.setHgrow(textField1, Priority.ALWAYS); // Set HBox to always grow horizontally
        textField1.setMaxWidth(Double.MAX_VALUE); // Set max width to allow extension
        // when user types something, then call textFieldChanged.handleChange(false);
        textField1.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
        	UIParameters.getInstance().setSearchText1(textField1.getText());
        	anySearchTextFieldChanged.handleChange("");// text in handleChange not used, we can set an empty string
        	verifyAndSetTextFieldStatusses();
        });
        /// add the fields to the HBox
        searchText1HBox.getChildren().addAll(searchText1Label, textField1);
        // add the hbox to the section
        completeSearchTextsVBox.getChildren().add(searchText1HBox);

        /// searchText2HBox
        searchText2HBox = new HBox();
        searchText2HBox.setSpacing(10);//  space between individual nodes in the Hbox
        searchText2HBox.setAlignment(Pos.CENTER_LEFT);
        GridPane.setHgrow(searchText2HBox, Priority.ALWAYS); // allows the Hbox to grow horizontally till the end of the grid
		// create label which gives explanation
        Label searchText2Label = new Label(labelGiveSearchText2String);
        UIUtilities.addToolTip(searchText2Label, labelGiveSearchText2String + labelGiveSearchTextToolTipString);
        /// the textfield that contains the searchtext
        textField2 = new TextField();
        textField2.setDisable(true);
        HBox.setHgrow(textField2, Priority.ALWAYS); // Set HBox to always grow horizontally
        textField2.setMaxWidth(Double.MAX_VALUE); // Set max width to allow extension
        // when user types something, then call textFieldChanged.handleChange(false);
        textField2.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
        	UIParameters.getInstance().setSearchText2(textField2.getText());
        	anySearchTextFieldChanged.handleChange("");// text in handleChange not used, we can set an empty string
        	verifyAndSetTextFieldStatusses();
        });
        /// add the fields to the HBox
        searchText2HBox.getChildren().addAll(searchText2Label, textField2);
        // add the hbox to the section
        completeSearchTextsVBox.getChildren().add(searchText2HBox);

        /// searchText3HBox
        searchText3HBox = new HBox();
        searchText3HBox.setSpacing(10);//  space between individual nodes in the Hbox
        searchText3HBox.setAlignment(Pos.CENTER_LEFT);
        GridPane.setHgrow(searchText3HBox, Priority.ALWAYS); // allows the Hbox to grow horizontally till the end of the grid
		// create label which gives explanation
        Label searchText3Label = new Label(labelGiveSearchText3String);
        UIUtilities.addToolTip(searchText3Label, labelGiveSearchText3String + labelGiveSearchTextToolTipString);
        /// the textfield that contains the searchtext
        textField3 = new TextField();
        textField3.setDisable(true);
        HBox.setHgrow(textField3, Priority.ALWAYS); // Set HBox to always grow horizontally
        textField3.setMaxWidth(Double.MAX_VALUE); // Set max width to allow extension
        // when user types something, then call textFieldChanged.handleChange(false);
        textField3.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
        	UIParameters.getInstance().setSearchText3(textField3.getText());
        	anySearchTextFieldChanged.handleChange("");// text in handleChange not used, we can set an empty string
        	verifyAndSetTextFieldStatusses();
        });
        /// add the fields to the HBox
        searchText3HBox.getChildren().addAll(searchText3Label, textField3);
        // add the hbox to the section
        completeSearchTextsVBox.getChildren().add(searchText3HBox);
        
        // add completeSearchTextsVBox to section
        section.getChildren().add(completeSearchTextsVBox);

        return section;
		
	}
	
    public static void addSearchToFolderWarning(String text) {UIUtilities.addWarningToVBox(completeWriteSearchToFolderVBox, searchToFolderHBoxWithLabelHBox, text, searchToFolderWarningLabel);}

	private static HBox createHboxWithWriteSearchToFolder(Stage primaryStage, TextFieldChanged textFieldChanged, String initialText) {
		
        String labelTextString = "Folder met zoekresultaten\u002A:\n";
        String labelTextWithExplanationString = "In deze folder komt een csv bestand met de zoekresultaten in.\n";
  
        return UIUtilities.createHBoxToSelectFolder(primaryStage, labelTextString, labelTextWithExplanationString, textFieldChanged, initialText, UIParameters.getInstance().getSourceTextFieldTextString());
        
	}

	
	private static void destFolderChangedHandler(String newFolder) {
		
        try {
        	
			List<String> backupFoldersAsStrings = ListBackupsInFolder.getAllBackupFoldersAsStrings(Paths.get(newFolder), "zzzz");
			
			if (backupFoldersAsStrings.size() > 0) {
				
				minimumStartSearchDate = OtherUtilities.getBackupDate(backupFoldersAsStrings.getLast(), processText); 
				maximumStartSearchDate = OtherUtilities.getBackupDate(backupFoldersAsStrings.getFirst(), processText); 
				
			} else {
				
				minimumStartSearchDate = null;
				maximumStartSearchDate = null;
				
			}
			
			
		} catch (IOException e) {
			if (processText != null) {
				processText.process("Fout bij het uitlezen van backup folders.");
				processText.process(e.toString());
			} else {
				System.out.println("Fout bij het uitlezen van backup folders.");
			}
			Thread.currentThread().interrupt();
			return;
		}
        

	}
	
	private static void verifyAndSetTextFieldStatusses() {
		if (textField1.getText().length() > 0) {
			textField2.setDisable(false);
			if (textField2.getText().length() > 0) {
				textField3.setDisable(false);
			} else {
				textField3.setDisable(true);
				textField3.setText("");
				UIParameters.getInstance().setSearchText3("");
			}
		} else {
			textField3.setDisable(true);
			textField3.setText("");
			UIParameters.getInstance().setSearchText3("");
			textField2.setDisable(true);
			textField2.setText("");
			UIParameters.getInstance().setSearchText2("");
		}
	}
	
	// Method to convert LocalDate to Date
    private static Date convertLocalDateToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
	
    // Method to convert Date to LocalDate
    private static LocalDate convertDateToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
    
    private static void setMinimumAndMaximumDate(DatePicker datePicker, Date minimumDate, Date maximumDate) {
    	
    	datePicker.setDayCellFactory(new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item.isBefore(convertDateToLocalDate(minimumStartSearchDate)) || item.isAfter(convertDateToLocalDate(maximumStartSearchDate))) {
                            setDisable(true);
                            setStyle("-fx-background-color: #ffc0cb;"); // Color for disabled dates
                        }
                    }
                };
            }
        });
    	
    }
    
}
