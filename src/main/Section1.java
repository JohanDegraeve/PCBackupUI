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

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Section1 {

	static HBox createSectionWithSourceAndDest(GridPane gridPane, Stage primaryStage, int row) {
		
        //// texts used
        String labelTextString = "Waar bevinden zich de oorspronkelijke bestanden:\n";
        String labelTextWithExplanationString = "Dit is de folder met de bron bestanden en folders,"
        		+ " dus de bestanden en folders die gebackupped worden. Ook als je een restore doet of\n"
        		+ "als je wilt zoeken in de backup, dan blijft dit de folder met de bron bestanden.\n";
  
        /// the HBox
        HBox hBox = new HBox();
        hBox.setSpacing(10);//  space between individual nodes in the Hbox
        hBox.setAlignment(Pos.CENTER_LEFT);
        GridPane.setHgrow(hBox, Priority.ALWAYS); // allows the Hbox to grow horizontally till the end of the grid
  
        /// the label to explain that source needs to be given
        Label sourceLabel = new Label(labelTextString ) ;
        Utilities.addToolTip(sourceLabel, labelTextString + labelTextWithExplanationString);
        
        /// the textfield that contains the selected source
        TextField sourceTextField = new TextField();
        sourceTextField.setPromptText("");
        HBox.setHgrow(sourceTextField, Priority.ALWAYS); // Set HBox to always grow horizontally
        sourceTextField.setMaxWidth(Double.MAX_VALUE); // Set max width to allow extension
        
        /// the button that allows selection of the source
        VBox sourceSelectionVBox = new VBox(Utilities.createButtonWithFileChooser("Kies", primaryStage, sourceTextField));

        /// add the fields to the HBox
        hBox.getChildren().addAll(sourceLabel, sourceSelectionVBox, sourceTextField);
        
        return hBox;
		
	}
	
}
