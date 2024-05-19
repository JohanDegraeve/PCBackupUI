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

import Enumerations.Action;
import Interfaces.ActionHandler;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utilities.UIUtilities;

public class Section2 {

	public static String fullBackupOptionsString = "Een volledige backup nemen";
	public static String incrementalBackupOptionString = "Een incrementele backup nemen";
	public static String restoreOptionsString = "Mappen herstellen";
	public static String searchOptionstrString = "Mappen en/of bestanden zoeken";

	@SuppressWarnings("exports")
	public static VBox createSection2(Stage primaryStage, ActionHandler handleAction) {
		
		VBox vBox = new VBox();
		vBox.setSpacing(5);
		
		HBox labelHBox = new HBox();
		Label mainLabel = new Label("Wat wil je doen? ");
		mainLabel.setStyle("-fx-font-weight: bold;");
		Label additionalInfoLabel = new Label("(Beweeg met de cursor over de teksten om meer uitleg te krijgen)");
		labelHBox.getChildren().addAll(mainLabel, additionalInfoLabel);
		
		// checkbox texts
		
		HBox hBox = new HBox();
        hBox.setSpacing(10);
     
        // Create three CheckBoxes for the options
        CheckBox fullBackupOption = new CheckBox(fullBackupOptionsString);
        UIUtilities.addToolTip(fullBackupOption, "Alle bestanden en mappen worden gekopieerd.");
        
        CheckBox incrementalBackupOption = new CheckBox(incrementalBackupOptionString);
        UIUtilities.addToolTip(incrementalBackupOption, "Enkel de bestanden en mappen die nieuw zijn of gewijzigd zijn sinds de vorige backup, worden gekopieerd.");

        CheckBox restoreOption = new CheckBox(restoreOptionsString);
        UIUtilities.addToolTip(restoreOption, "Mappen en de bestanden in die mappen worden hersteld.");

        CheckBox searchOption = new CheckBox(searchOptionstrString);
        UIUtilities.addToolTip(searchOption, "Zoek naar bestanden en mappen die een specifieke tekst in de naam van de map of bestand hebben.");
        
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
		
		// this means all checkboxes are unchecked, call handleAction with action null
		if (!clickedCheckBox.isSelected()) {
			handleAction.handleAction(null);
			return;
		}
		
		if (clickedCheckBox.getText() == fullBackupOptionsString) {
			handleAction.handleAction(Action.FULLBACKUP);
		} else if (clickedCheckBox.getText() == incrementalBackupOptionString) {
			handleAction.handleAction(Action.INCREMENTALBACKUP);
		} else if (clickedCheckBox.getText() == searchOptionstrString) {
			handleAction.handleAction(Action.SEARCH);
		} else if (clickedCheckBox.getText() == restoreOptionsString) {
			handleAction.handleAction(Action.RESTORE);
		}
		
	}
	
}
