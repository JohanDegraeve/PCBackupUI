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

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Utilities {

	static void addToolTip(Node node, String toolTipTextString) {
        Tooltip tooltip = new Tooltip(toolTipTextString);
        tooltip.setShowDuration(new Duration(60000));
        Tooltip.install(node, tooltip);
    }
	
	static Button createButtonWithDirectoryChooser(String buttonTextString, Stage stage, TextField textField) {
		
		Button selectFolderButton = new Button(buttonTextString);

        // Add an action listener to the button
        selectFolderButton.setOnAction(e -> {
        	
            // Create a DirectoryChooser
            DirectoryChooser directoryChooser = new DirectoryChooser();

            // Set title for the directory chooser dialog
            directoryChooser.setTitle("Kies map");

            // Show the dialog and wait for user input
            File selectedDirectory = directoryChooser.showDialog(stage);

            // Handle the selected directory
            if (selectedDirectory != null) {
                // Print the path of the selected directory
                textField.setText(selectedDirectory.getAbsolutePath());
            } else {
                
            }
        });
        
        return selectFolderButton;
		
	}
	 
	static Button createButtonWithFileChooser(String buttonTextString, Stage stage, TextField textField) {
		
		Button selectFolderButton = new Button(buttonTextString);

        // Add an action listener to the button
        selectFolderButton.setOnAction(e -> {
        	
            // Create a DirectoryChooser
        	FileChooser fileChooser = new FileChooser();

            // Set title for the directory chooser dialog
        	fileChooser.setTitle("Kies bestand");

            // Show the dialog and wait for user input
            File selectedFile = fileChooser.showOpenDialog(stage);

            // Handle the selected directory
            if (selectedFile != null) {
                // Print the path of the selected directory
                textField.setText(selectedFile.getAbsolutePath());
            } else {
                
            }
        });
        
        return selectFolderButton;
		
	}
	 
	static HBox createHBoxToSelectFile(Stage primaryStage, String labelTextString, String labelTextWithExplanationString) {
		
		return createHBoxToSelectFolderOrFile(primaryStage, labelTextString, labelTextWithExplanationString, "Kies", (buttonTextString, stage, textField) -> createButtonWithFileChooser(buttonTextString, stage, textField));
		
	}
	
	static HBox createHBoxToSelectFolder(Stage primaryStage, String labelTextString, String labelTextWithExplanationString) {

		return createHBoxToSelectFolderOrFile(primaryStage, labelTextString, labelTextWithExplanationString, "Kies", (buttonTextString, stage, textField) -> createButtonWithDirectoryChooser(buttonTextString, stage, textField));

	}
	
	private static HBox createHBoxToSelectFolderOrFile(Stage primaryStage, String labelTextString, String labelTextWithExplanationString, String buttonTextString, ButtonCreator buttonCreator) {
		
        /// the HBox
        HBox hBox = new HBox();
        hBox.setSpacing(10);//  space between individual nodes in the Hbox
        hBox.setAlignment(Pos.CENTER_LEFT);
        GridPane.setHgrow(hBox, Priority.ALWAYS); // allows the Hbox to grow horizontally till the end of the grid
  
        /// the label to explain that source needs to be given
        Label label = new Label(labelTextString );
        Utilities.addToolTip(label, labelTextString + labelTextWithExplanationString);
        
        /// the textfield that contains the selected source
        TextField textField = new TextField();
        textField.setPromptText("");
        HBox.setHgrow(textField, Priority.ALWAYS); // Set HBox to always grow horizontally
        textField.setMaxWidth(Double.MAX_VALUE); // Set max width to allow extension
        
        /// the button that allows selection of the source
        VBox folderSelectionVBox = new VBox(buttonCreator.createButton(buttonTextString, primaryStage, textField));

        /// add the fields to the HBox
        hBox.getChildren().addAll(label, folderSelectionVBox, textField);
        
        return hBox;

	}
	
	@FunctionalInterface
	interface ButtonCreator {
        Button createButton(String buttonTextString, Stage stage, TextField textField);
    }
	
}
