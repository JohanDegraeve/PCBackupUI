package main;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SectionBackupParameters {

	public static VBox createSectionBackupParameters(Stage primaryStage) {

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
        
        HBox hbox = Utilities.createHBoxToSelectFile(primaryStage, labelTextSelectExcludedFileList, labelTextWithExplanationSelectExcludedFileList, null);
        hbox.setSpacing(10);
        hbox.getChildren().add(new Pane()); // Placeholder content
        section.getChildren().add(hbox);

        hbox = Utilities.createHBoxToSelectFolder(primaryStage, labelTextSelectExcludedPathList, labelTextSelectWithExplanationExcludedPathList, null);
        hbox.setSpacing(10);
        hbox.getChildren().add(new Pane()); // Placeholder content
        section.getChildren().add(hbox);

        hbox = Utilities.createHBoxToSelectFolder(primaryStage, labelTextSelectedFolderNameMappingList, labelTextWithExplanationSelectedFolderNameMappingList, null);
        hbox.setSpacing(10);
        hbox.getChildren().add(new Pane()); // Placeholder content
        section.getChildren().add(hbox);

        return section;
        
	}
	
}
