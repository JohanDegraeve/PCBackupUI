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
package utilities;

import java.io.File;

import Interfaces.ButtonCreator;
import Interfaces.TextFieldChanged;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

public class UIUtilities {

	public static void addToolTip(Node node, String toolTipTextString) {
        Tooltip tooltip = new Tooltip(toolTipTextString);
        tooltip.setShowDuration(new Duration(60000));
        Tooltip.install(node, tooltip);
    }
	
	public static Button createButtonWithDirectoryChooser(String buttonTextString, Stage stage, TextField textField, TextFieldChanged textFieldChanged) {
		
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
                if (textFieldChanged != null) {
                    textFieldChanged.handleChange(selectedDirectory.getAbsolutePath());
                }
            } else {
                if (textFieldChanged != null) {
                    textFieldChanged.handleChange(null);
                }
            }
        });
        
        return selectFolderButton;
		
	}
	 
	public static Button createButtonWithFileChooser(String buttonTextString, Stage stage, TextField textField, TextFieldChanged textFieldChanged) {
		
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
                if (textFieldChanged != null) {
                    textFieldChanged.handleChange(selectedFile.getAbsolutePath());
                }
            } else {
            	if (textFieldChanged != null) {
                    textFieldChanged.handleChange(null);
                }
            }
        });
        
        return selectFolderButton;
		
	}
	 
	public static HBox createHBoxToSelectFile(Stage primaryStage, String labelTextString, String labelTextWithExplanationString, TextFieldChanged selectFileFieldChanged) {
		
		return createHBoxToSelectFolderOrFile(primaryStage, labelTextString, labelTextWithExplanationString, "Kies", (buttonTextString, stage, textField, textFieldChanged) -> createButtonWithFileChooser(buttonTextString, stage, textField, textFieldChanged), selectFileFieldChanged);
		
	}
	
	public static HBox createHBoxToSelectFolder(Stage primaryStage, String labelTextString, String labelTextWithExplanationString, TextFieldChanged selectFolderFieldChanged) {

		return createHBoxToSelectFolderOrFile(primaryStage, labelTextString, labelTextWithExplanationString, "Kies", (buttonTextString, stage, textField, textFieldChanged) -> createButtonWithDirectoryChooser(buttonTextString, stage, textField, textFieldChanged), selectFolderFieldChanged);

	}
	
    /**
     * adds a HBox with a label  to the vBox, with red text<br>
     * if vBox already has two nodes, then nothing is changed<br>
     * if text = null or empty string then the label is removed
     * @param vbox
     * @param text
     */
	public static void addWarningToVBox(VBox vbox, HBox hboxWithLabel, String text, Label label) {
    	
    	if (text == null) {removeWarningFromVBox(vbox, hboxWithLabel);return;};
    	if (text.length() == 0) {removeWarningFromVBox(vbox, hboxWithLabel);return;};
    	
    	if (vbox.getChildren().size() > 1) {return;}
    	
		label.setText(text);
		vbox.getChildren().add(hboxWithLabel);
    }
	
    private static void removeWarningFromVBox(VBox vBox, HBox hboxWithLabel) {
    	if (vBox.getChildren().size() == 1) {return;}
    	vBox.getChildren().remove(hboxWithLabel);
    }
	
	private static HBox createHBoxToSelectFolderOrFile(Stage primaryStage, String labelTextString, String labelTextWithExplanationString, String buttonTextString, ButtonCreator buttonCreator, TextFieldChanged textFieldChanged) {
		
        /// the HBox
        HBox hBox = new HBox();
        hBox.setSpacing(10);//  space between individual nodes in the Hbox
        hBox.setAlignment(Pos.CENTER_LEFT);
        GridPane.setHgrow(hBox, Priority.ALWAYS); // allows the Hbox to grow horizontally till the end of the grid
  
        /// the label to explain that source needs to be given
        Label label = new Label(labelTextString );
        UIUtilities.addToolTip(label, labelTextString + labelTextWithExplanationString);
        
        /// the textfield that contains the selected source
        TextField textField = new TextField();
        textField.setPromptText("");
        HBox.setHgrow(textField, Priority.ALWAYS); // Set HBox to always grow horizontally
        textField.setMaxWidth(Double.MAX_VALUE); // Set max width to allow extension
        
        // when user types something, then call textFieldChanged.handleChange(false);
        textField.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            textFieldChanged.handleChange(textField.getText());
        });
        
        /// the button that allows selection of the source
        VBox folderSelectionVBox = new VBox(buttonCreator.createButton(buttonTextString, primaryStage, textField, textFieldChanged));

        /// add the fields to the HBox
        hBox.getChildren().addAll(label, folderSelectionVBox, textField);
        
        return hBox;

	}
	
}
