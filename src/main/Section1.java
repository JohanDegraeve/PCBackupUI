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

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Section1 {

    public static VBox createSection1(Stage primaryStage) {
    	
        VBox section = new VBox();
        section.setSpacing(5);

		HBox labelHBox = new HBox();
		Label mainLabel = new Label("Specifieer de mappen. ");
		mainLabel.setStyle("-fx-font-weight: bold;");
		Label additionalInfoLabel = new Label("(Beweeg met de muis over de teksten om meer uitleg te krijgen)");
		labelHBox.getChildren().addAll(mainLabel, additionalInfoLabel);
		section.getChildren().add(labelHBox);
				
        HBox hbox = createHboxWithSource(primaryStage);
        hbox.setSpacing(10);
        hbox.getChildren().add(new Pane()); // Placeholder content
        section.getChildren().add(hbox);
        
        hbox = createHboxWitDest(primaryStage);
        hbox.setSpacing(10);
        hbox.getChildren().add(new Pane()); // Placeholder content
        section.getChildren().add(hbox);
        
        hbox = createHboxWitLogFileFolder(primaryStage);
        hbox.setSpacing(10);
        hbox.getChildren().add(new Pane()); // Placeholder content
        section.getChildren().add(hbox);
        
        return section;
    }
	
	private static HBox createHboxWithSource(Stage primaryStage) {
		
        String labelTextString = "Waar bevinden zich de oorspronkelijke bestanden:\n";
        String labelTextWithExplanationString = "Dit is de folder met de bron bestanden en folders,"
        		+ " dus de bestanden en folders die gebackupped worden. Ook als je een restore doet of\n"
        		+ "als je wilt zoeken in de backup, dan blijft dit de folder met de bron bestanden.\n";
  
        return Utilities.createHBoxToSelectFolder(primaryStage, labelTextString, labelTextWithExplanationString);
        
	}
	
	private static HBox createHboxWitDest(Stage primaryStage) {
		
        String labelTextString = "Waar bevinden zich de backup folders:\n";
        String labelTextWithExplanationString = "Dit is de folder waar de backups komen."
        		+ "Elke nieuwe incrementele of volledige backup komt in een subfolder van deze folder.\n"
        		+ "Restores gebeuren vanuit deze backup folders. Zoeken naar bestanden gebeurt ook in deze backup folders.\n";
  
        return Utilities.createHBoxToSelectFolder(primaryStage, labelTextString, labelTextWithExplanationString);
        
	}
	
	private static HBox createHboxWitLogFileFolder(Stage primaryStage) {
		
        String labelTextString = "In welke folder mogen de logs weggeschreven worden:\n";
        String labelTextWithExplanationString = "Dit is de folder waar de logs komen."
        		+ "De logs zijn tekst bestanden die info geven over het backup proces.\n";
  
        return Utilities.createHBoxToSelectFolder(primaryStage, labelTextString, labelTextWithExplanationString);
        
	}

}
