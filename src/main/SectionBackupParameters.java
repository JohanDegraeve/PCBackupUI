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

import Interfaces.TextFieldChanged;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.UIParameters;
import utilities.UIUtilities;

public class SectionBackupParameters {

	private static VBox completeExcludedFileListVBox;
	private static HBox excludedFileListHBoxWithFileText;
	private static HBox excludedFileListHBoxWithLabelHBox;
	private static Label excludedFileListWarningLabel;

	private static VBox completeExcludedPathListVBox;
	private static HBox excludedPathListHBoxWithFileText;
	private static HBox excludedPathListHBoxWithLabelHBox;
	private static Label excludedPathListWarningLabel;

	private static VBox completeFolderNameMappingListVBox;
	private static HBox folderNameMappingListHBoxWithFileText;
	private static HBox folderNameMappingListHBoxWithLabelHBox;
	private static Label folderNameMappingListWarningLabel;

	@SuppressWarnings("exports")
	public static VBox createSectionBackupParameters(Stage primaryStage, TextFieldChanged excludedFileListChanged, TextFieldChanged excludedPathListChanged, TextFieldChanged folderNameMappingListChanged, String initialTextExcludedFile, String initialTextExclucedPath, String initialTextFolderNameMapping) {

		// texts for excluded file list
        String labelTextSelectExcludedFileList = "Selecteer bestand met uit te sluiten bestanden (optioneel):\n";
        String labelTextWithExplanationSelectExcludedFileList = "Dit is een tekst bestand en bevat een lijst met bestanden die niet moeten gebackupped worden";

        // texts for excluced path list
        String labelTextSelectExcludedPathList = "Selecteer bestand met uit te sluiten mappen (optioneel):\n";
        String labelTextSelectWithExplanationExcludedPathList = "Dit is een tekst bestand en bevat een lijst met mappen die niet moeten gebackupped worden.\n" 
        		+ "Dit geldt enkel voor mappen in de backup map, en niet de mappen daaronder. Dus enkel voor de eerste map.";

        // texts for foldername mapping
        String labelTextSelectedFolderNameMappingList = "Selecteer bestand met map namen die moeten aangepast worden (optioneel):\n";
        String labelTextWithExplanationSelectedFolderNameMappingList = "Dit is een tekst bestand en bevat een lijst met mappen waarvan de naam moet aangepast worden in de backup.\n" 
        		+ "Dit geldt enkel voor mappen in de backup map, en niet de mappen daaronder. Dus enkel voor de eerste map.";
        
		VBox section = new VBox();
        section.setSpacing(5);
        
        completeExcludedFileListVBox = new VBox();
        completeExcludedPathListVBox = new VBox();
        completeFolderNameMappingListVBox = new VBox();
        
        
        excludedFileListHBoxWithFileText = UIUtilities.createHBoxToSelectFile(primaryStage, labelTextSelectExcludedFileList, labelTextWithExplanationSelectExcludedFileList, excludedFileListChanged, initialTextExcludedFile, UIParameters.getInstance().getExcludedFileListTextFieldTextString());
        excludedFileListHBoxWithFileText.setSpacing(10);
        completeExcludedFileListVBox.getChildren().add(excludedFileListHBoxWithFileText);
        section.getChildren().add(completeExcludedFileListVBox);

        excludedPathListHBoxWithFileText = UIUtilities.createHBoxToSelectFile(primaryStage, labelTextSelectExcludedPathList, labelTextSelectWithExplanationExcludedPathList, excludedPathListChanged, initialTextExclucedPath, UIParameters.getInstance().getExcludedPathListTextFieldTextString());
        excludedPathListHBoxWithFileText.setSpacing(10);
        completeExcludedPathListVBox.getChildren().add(excludedPathListHBoxWithFileText);
        section.getChildren().add(completeExcludedPathListVBox);

        folderNameMappingListHBoxWithFileText = UIUtilities.createHBoxToSelectFile(primaryStage, labelTextSelectedFolderNameMappingList, labelTextWithExplanationSelectedFolderNameMappingList, folderNameMappingListChanged, initialTextFolderNameMapping, UIParameters.getInstance().getFolderNameMappingTextFieldTextString());
        folderNameMappingListHBoxWithFileText.setSpacing(10);
        completeFolderNameMappingListVBox.getChildren().add(folderNameMappingListHBoxWithFileText);
        section.getChildren().add(completeFolderNameMappingListVBox);

        // intialize label and hbox that will contain the label
        excludedFileListWarningLabel = new Label();
        excludedFileListWarningLabel.setStyle("-fx-text-fill: red;");
        excludedFileListHBoxWithLabelHBox = new HBox();
        excludedFileListHBoxWithLabelHBox.getChildren().add(excludedFileListWarningLabel);
        
        excludedPathListWarningLabel = new Label();
        excludedPathListWarningLabel.setStyle("-fx-text-fill: red;");
        excludedPathListHBoxWithLabelHBox = new HBox();
        excludedPathListHBoxWithLabelHBox.getChildren().add(excludedPathListWarningLabel);
        
        folderNameMappingListWarningLabel = new Label();
        folderNameMappingListWarningLabel.setStyle("-fx-text-fill: red;");
        folderNameMappingListHBoxWithLabelHBox = new HBox();
        folderNameMappingListHBoxWithLabelHBox.getChildren().add(folderNameMappingListWarningLabel);
        
        
        return section;
        
	}
	
    public static void addExcludedFileListWarning(String text) {UIUtilities.addWarningToVBox(completeExcludedFileListVBox, excludedFileListHBoxWithLabelHBox, text, excludedFileListWarningLabel);}

    public static void addExcludedPathListWarning(String text) {UIUtilities.addWarningToVBox(completeExcludedPathListVBox, excludedPathListHBoxWithLabelHBox, text, excludedPathListWarningLabel);}

    public static void addFolerNameMappingListWarning(String text) {UIUtilities.addWarningToVBox(completeFolderNameMappingListVBox, folderNameMappingListHBoxWithLabelHBox, text, folderNameMappingListWarningLabel);}

}
