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

import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Section1 {

	static HBox createHboxWithSource(GridPane gridPane, Stage primaryStage) {
		
        //// texts used
        String labelTextString = "Waar bevinden zich de oorspronkelijke bestanden:\n";
        String labelTextWithExplanationString = "Dit is de folder met de bron bestanden en folders,"
        		+ " dus de bestanden en folders die gebackupped worden. Ook als je een restore doet of\n"
        		+ "als je wilt zoeken in de backup, dan blijft dit de folder met de bron bestanden.\n";
  
        return Utilities.createHBoxToSelectFolder(gridPane, primaryStage, labelTextString, labelTextWithExplanationString);
        
	}
	
	static HBox createHboxWitDest(GridPane gridPane, Stage primaryStage) {
		
        //// texts used
        String labelTextString = "Waar bevinden zich de backup folders:\n";
        String labelTextWithExplanationString = "Dit is de folder waar de bacups komen."
        		+ "Elke nieuwe incrementele of volledige backup komt in een subfolder van deze folder.\n"
        		+ "Restores gebeuren vanuit deze backup folders. Zoeken naar bestanden gebeurt ook in deze backup folders.\n";
  
        return Utilities.createHBoxToSelectFolder(gridPane, primaryStage, labelTextString, labelTextWithExplanationString);
        
	}
	
	static HBox createHboxWitLogFileFolder(GridPane gridPane, Stage primaryStage) {
		
        //// texts used
        String labelTextString = "In welke folder mogen de logs weggeschreven worden:\n";
        String labelTextWithExplanationString = "Dit is de folder waar de logs komen."
        		+ "De logs zijn tekst bestanden die info geven over het backup proces.\n";
  
        return Utilities.createHBoxToSelectFolder(gridPane, primaryStage, labelTextString, labelTextWithExplanationString);
        
	}

}
