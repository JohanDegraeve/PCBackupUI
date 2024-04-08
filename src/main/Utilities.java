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

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Utilities {

	static void addToolTip(Node node, String toolTipTextString) {
        Tooltip tooltip = new Tooltip(toolTipTextString);
        tooltip.setShowDuration(new Duration(60000));
        Tooltip.install(node, tooltip);
    }
	
	static Button createButtonWithFileChooser(String buttonTextString, Stage stage, TextField textField) {
		
		Button selectFolderButton = new Button(buttonTextString);

        // Add an action listener to the button
        selectFolderButton.setOnAction(e -> {
        	
            // Create a DirectoryChooser
            DirectoryChooser directoryChooser = new DirectoryChooser();

            // Set title for the directory chooser dialog
            directoryChooser.setTitle("Select Folder");

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
	
}
