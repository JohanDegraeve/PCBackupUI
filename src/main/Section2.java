package main;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.Main.ActionHandler;

public class Section2 {

	static String fullBackupOptionsString = "Een volledige backup nemen";
	static String incrementalBackupOptionString = "Een incrementele backup nemen";
	static String restoreOptionsString = "Mappen herstellen";
	static String searchOptionstrString = "Mappen en/of bestanden zoeken";

	public static VBox createSection2(Stage primaryStage, ActionHandler handleAction) {
		
		VBox vBox = new VBox();
		vBox.setSpacing(5);
		
		HBox labelHBox = new HBox();
		Label mainLabel = new Label("Wat wil je doen? ");
		mainLabel.setStyle("-fx-font-weight: bold;");
		Label additionalInfoLabel = new Label("(Beweeg met de muis over de teksten om meer uitleg te krijgen.)");
		labelHBox.getChildren().addAll(mainLabel, additionalInfoLabel);
		
		// checkbox texts
		
		HBox hBox = new HBox();
        hBox.setSpacing(10);
     
        // Create three CheckBoxes for the options
        CheckBox fullBackupOption = new CheckBox(fullBackupOptionsString);
        Utilities.addToolTip(fullBackupOption, "Alle bestanden en mappen worden gekopieerd.");
        
        CheckBox incrementalBackupOption = new CheckBox(incrementalBackupOptionString);
        Utilities.addToolTip(incrementalBackupOption, "Enkel de bestanden en mappen die nieuw zijn of gewijzigd zijn sinds de vorige backup, worden gekopieerd.");

        CheckBox restoreOption = new CheckBox(restoreOptionsString);
        Utilities.addToolTip(restoreOption, "Mappen en de bestanden in die mappen worden hersteld.");

        CheckBox searchOption = new CheckBox(searchOptionstrString);
        Utilities.addToolTip(searchOption, "Zoek naar bestanden en mappen die een specifieke tekst in de naam van de map of bestand hebben.");
        
        // Add event handlers to the CheckBoxes
        fullBackupOption.setOnAction(e -> handleCheckBox(fullBackupOption, handleAction, incrementalBackupOption, restoreOption, searchOption));
        incrementalBackupOption.setOnAction(e -> handleCheckBox(incrementalBackupOption, handleAction, fullBackupOption, restoreOption, searchOption));
        restoreOption.setOnAction(e -> handleCheckBox(restoreOption, handleAction, fullBackupOption, incrementalBackupOption, searchOption));
        searchOption.setOnAction(e -> handleCheckBox(searchOption, handleAction, restoreOption, fullBackupOption, incrementalBackupOption));
        
        // Add CheckBoxes to the HBox
        hBox.getChildren().addAll(fullBackupOption, incrementalBackupOption, restoreOption, searchOption);

        vBox.getChildren().addAll(labelHBox, hBox);
        

        return vBox;
        
	}
	
	private static void handleCheckBox(CheckBox clickedCheckBox, ActionHandler handleAction, CheckBox ...otherCheckBoxs) {
		
		// set the other checkboxes unchecked
		for (CheckBox checkBox: otherCheckBoxs) {
			checkBox.setSelected(false);
		}
		
		if (clickedCheckBox.getText() == fullBackupOptionsString) {
			handleAction.handleAction(Action.FULLBACKUP);
		}
		
	}
	
}