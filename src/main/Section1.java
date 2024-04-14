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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Section1 {

	private static VBox completeSourceVBox;
	private static HBox sourceHBoxWithFolderText;
	private static HBox sourceHBoxWithLabelHBox;
	private static Label sourceWarningLabel;
	
	private static VBox completeDestVBox;
	private static HBox destHBoxWithFolderText;
	private static HBox destHBoxWithLabelHBox;
	private static Label destWarningLabel;
	
	private static VBox completeLogFolderVBox;
	private static HBox logfolderHBoxWithFolderText;
	private static HBox logfolderHBoxWithLabelHBox;
	private static Label logFolderWarningLabel;
	
    public static VBox createSection1(Stage primaryStage, TextFieldChanged sourceChanged, TextFieldChanged destChanged, TextFieldChanged logfilefolderChanged) {
    	
        VBox section = new VBox();
        section.setSpacing(5);

		HBox labelHBox = new HBox();
		Label mainLabel = new 
				Label("Specifieer de mappen. ");
		mainLabel.setStyle("-fx-font-weight: bold;");
		Label additionalInfoLabel = new Label("(Beweeg met de muis over de teksten om meer uitleg te krijgen)");
		labelHBox.getChildren().addAll(mainLabel, additionalInfoLabel);
		section.getChildren().add(labelHBox);
				
		completeSourceVBox = new VBox();
		completeDestVBox = new VBox();
		completeLogFolderVBox = new VBox();
		
        sourceHBoxWithFolderText = createHboxWithSource(primaryStage, sourceChanged);
        sourceHBoxWithFolderText.setSpacing(10);
        
        completeSourceVBox.getChildren().add(sourceHBoxWithFolderText);
        section.getChildren().add(completeSourceVBox);
        
        destHBoxWithFolderText = createHboxWitDest(primaryStage, destChanged);
        destHBoxWithFolderText.setSpacing(10);
        completeDestVBox.getChildren().add(destHBoxWithFolderText);
        section.getChildren().add(completeDestVBox);
        
        logfolderHBoxWithFolderText = createHboxWitLogFileFolder(primaryStage,logfilefolderChanged);
        logfolderHBoxWithFolderText.setSpacing(10);
        completeLogFolderVBox.getChildren().add(logfolderHBoxWithFolderText);
        section.getChildren().add(completeLogFolderVBox);
        
        
        // intialize label and hbox that will contain the label
        sourceWarningLabel = new Label();
        sourceWarningLabel.setStyle("-fx-text-fill: red;");
        sourceHBoxWithLabelHBox = new HBox();
        sourceHBoxWithLabelHBox.getChildren().add(sourceWarningLabel);

        destWarningLabel = new Label();
        destWarningLabel.setStyle("-fx-text-fill: red;");
        destHBoxWithLabelHBox = new HBox();
        destHBoxWithLabelHBox.getChildren().add(destWarningLabel);
        
        logFolderWarningLabel = new Label();
        logFolderWarningLabel.setStyle("-fx-text-fill: red;");
        logfolderHBoxWithLabelHBox = new HBox();
        logfolderHBoxWithLabelHBox.getChildren().add(logFolderWarningLabel);
        
        return section;
    }
    
    public static void addSourceWarning(String text) {addWarningToVBox(completeSourceVBox, sourceHBoxWithLabelHBox, text, sourceWarningLabel);}
    
    public static void addDestWarning(String text) {addWarningToVBox(completeDestVBox, destHBoxWithLabelHBox, text, destWarningLabel);}
    
    public static void addLogFolderWarning(String text) {addWarningToVBox(completeLogFolderVBox, logfolderHBoxWithLabelHBox, text, logFolderWarningLabel);}
    
    /**
     * adds a HBox with a label  to the vBox, with red text<br>
     * if vBox already has two nodes, then nothing is changed<br>
     * if text = null or empty string then the label is removed
     * @param vbox
     * @param text
     */
    private static void addWarningToVBox(VBox vbox, HBox hboxWithLabel, String text, Label label) {
    	
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
    
	private static HBox createHboxWithSource(Stage primaryStage, TextFieldChanged textFieldChanged) {
		
        String labelTextString = "Waar bevinden zich de oorspronkelijke bestanden\u002A:\n";
        String labelTextWithExplanationString = "Dit is de folder met de bron bestanden en folders,"
        		+ " dus de bestanden en folders die gebackupped worden. Ook als je een restore doet of\n"
        		+ "als je wilt zoeken in de backup, dan blijft dit de folder met de bron bestanden.\n";
  
        return Utilities.createHBoxToSelectFolder(primaryStage, labelTextString, labelTextWithExplanationString, textFieldChanged);
        
	}
	
	private static HBox createHboxWitDest(Stage primaryStage, TextFieldChanged textFieldChanged) {
		
        String labelTextString = "Waar bevinden zich de backup folders\u002A:\n";
        String labelTextWithExplanationString = "Dit is de folder waar de backups komen."
        		+ "Elke nieuwe incrementele of volledige backup komt in een subfolder van deze folder.\n"
        		+ "Restores gebeuren vanuit deze backup folders. Zoeken naar bestanden gebeurt ook in deze backup folders.\n";
  
        return Utilities.createHBoxToSelectFolder(primaryStage, labelTextString, labelTextWithExplanationString, textFieldChanged);
        
	}
	
	private static HBox createHboxWitLogFileFolder(Stage primaryStage, TextFieldChanged textFieldChanged) {
		
        String labelTextString = "In welke folder mogen de logs weggeschreven worden\u002A:\n";
        String labelTextWithExplanationString = "Dit is de folder waar de logs komen."
        		+ "De logs zijn tekst bestanden die info geven over het backup proces.\n";
  
        return Utilities.createHBoxToSelectFolder(primaryStage, labelTextString, labelTextWithExplanationString, textFieldChanged);
        
	}

}
