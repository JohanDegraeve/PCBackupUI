package main;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import Interfaces.FolderChangedHolder;
import Interfaces.ProcessText;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.DateCell;
import javafx.util.Callback;
import utilities.ListBackupsInFolder;
import utilities.OtherUtilities;
import utilities.UIUtilities;

public class SectionSearchParameters {

	static final String defaultDateString = "Geen datum geselecteerd";
	
	// keeps the currently selected date, initially set to no null
	static Date selectedDate = null;
	
	/// Global variables
	private static ProcessText processText;
	private static Stage stage;
	
	private static VBox completeSelectStartSearchDateVBox;
	private static HBox selectStartSearchDateHBox;
	private static Button startSearchDateButton;
	
	private static Date minimumStartSearchDate;
	private static Date maximumStartSearchDate;

	/**
	 * 
	 * @param stage
	 * @param processText
     * @param destFolderChangedHolder is the folder where backup subfolders are stored, that folder is needed here because we get all backup folder names to determine the earlieast and latest search date
	 * @return
	 */
	@SuppressWarnings("exports")
	public static VBox createSectionSearchParameters(Stage stage, ProcessText processText, FolderChangedHolder destFolderChangedHolder) {
	
		String labelSelectStartSearchDate = "Vanaf welke datum wil je zoeken in de backups:\n";
		String labelSelectStartSearchDateToolTipString = "Er zal gezocht worden in all backups die gemaakt zijn op of na deze dataum.\n" +
		"Je kunt dit veld leeglaten, dan zal er gezocht worden vanaf de eerste backup.";
		
		String labelSelectEndSearchDate = "Tot welke datum wil je zoeken in de backups:";
		String labelSelectEndSearchDateToolTipString = "Er zal gezocht worden in all backups die gemaakt zijn op of voor deze dataum.\n" +
		"Je kunt dit veld leeglaten, dan zal er gezocht worden tot en met de laatste backup.\n" + 
		"Klik om de datum aan te passen";
		
		destFolderChangedHolder.folderChanged = (String folder) -> destFolderChangedHandler(folder);

		SectionSearchParameters.processText = processText;
		SectionSearchParameters.stage = stage;
		
		VBox section = new VBox();
        section.setSpacing(5);
        
        completeSelectStartSearchDateVBox = new VBox();
		
		////////////////////////////////////////////////////////////
		//////         CREATE STARTSEARCHDATE VBOX           ///////
		////////////////////////////////////////////////////////////
		
		// create label which gives explanation
        Label label = new Label(labelSelectStartSearchDate);
        UIUtilities.addToolTip(label, labelSelectStartSearchDate + labelSelectStartSearchDateToolTipString);
        
        DatePicker datePicker = new DatePicker();
        // set minimum and maximum date
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
        
        startSearchDateButton = new Button(createDateButtonString(selectedDate));
        startSearchDateButton.setOnAction(event -> {
        	datePicker.requestFocus();
            datePicker.show();
        });
        
        datePicker.setOnAction(event -> {
            selectedDate = convertLocalDateToDate(datePicker.getValue());
            startSearchDateButton.setText(createDateButtonString(selectedDate));
        });
        
        selectStartSearchDateHBox = new HBox();
		selectStartSearchDateHBox.setSpacing(10);//  space between individual nodes in the Hbox
		selectStartSearchDateHBox.setAlignment(Pos.CENTER_LEFT);
	    GridPane.setHgrow(selectStartSearchDateHBox, Priority.ALWAYS);
		selectStartSearchDateHBox.getChildren().addAll(label, startSearchDateButton, datePicker);
		completeSelectStartSearchDateVBox.getChildren().add(selectStartSearchDateHBox);
		section.getChildren().add(completeSelectStartSearchDateVBox);
		
		return section;
		
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
	
	// Method to convert LocalDate to Date
    private static Date convertLocalDateToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
	
    // Method to convert Date to LocalDate
    private static LocalDate convertDateToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
    
    /**
     * 
     * @param date
     * @return
     */
    private static String createDateButtonString(Date date) {
    	if (date == null) {return defaultDateString;}
    	
    	return OtherUtilities.dateToString(date, "dd MMM yyyy");
    	
    }
    
}
