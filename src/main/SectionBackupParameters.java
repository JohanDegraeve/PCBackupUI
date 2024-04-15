package main;

import Interfaces.TextFieldChanged;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
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

	public static VBox createSectionBackupParameters(Stage primaryStage, TextFieldChanged excludedFileListChanged, TextFieldChanged excludedPathListChanged, TextFieldChanged folderNameMappingListChanged) {

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
        
        
        excludedFileListHBoxWithFileText = UIUtilities.createHBoxToSelectFile(primaryStage, labelTextSelectExcludedFileList, labelTextWithExplanationSelectExcludedFileList, excludedFileListChanged);
        excludedFileListHBoxWithFileText.setSpacing(10);
        completeExcludedFileListVBox.getChildren().add(excludedFileListHBoxWithFileText);
        section.getChildren().add(completeExcludedFileListVBox);

        excludedPathListHBoxWithFileText = UIUtilities.createHBoxToSelectFile(primaryStage, labelTextSelectExcludedPathList, labelTextSelectWithExplanationExcludedPathList, excludedPathListChanged);
        excludedPathListHBoxWithFileText.setSpacing(10);
        completeExcludedPathListVBox.getChildren().add(excludedPathListHBoxWithFileText);
        section.getChildren().add(completeExcludedPathListVBox);

        folderNameMappingListHBoxWithFileText = UIUtilities.createHBoxToSelectFile(primaryStage, labelTextSelectedFolderNameMappingList, labelTextWithExplanationSelectedFolderNameMappingList, folderNameMappingListChanged);
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
