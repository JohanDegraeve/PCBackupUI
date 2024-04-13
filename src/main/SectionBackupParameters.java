package main;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SectionBackupParameters {

	public static VBox createSectionBackupParameters(Stage primaryStage) {

        String labelTextSelectExcludedFileList = "Selecteer bestand met uit te sluiten bestanden:\n";
        String labelTextWithExplanationSelectExcludedFileList = "Dit is een tekst bestand en bevat een lijst met bestanden die niet moeten gebackupped worden";

        String labelTexSelectedExcludedPathList = "Selecteer bestand met uit te sluiten mappen:\n";
        String labelTexSelectedWithExplanationExcludedPathList = "Dit is een tekst bestand en bevat een lijst met mappen die niet moeten gebackupped worden.\n" 
        		+ "Dit geldt enkel voor mappen in de backup map, en niet de mappen daaronder. Dus enkel voor de eerste map.";

		VBox section = new VBox();
        section.setSpacing(5);
        
        HBox hbox = Utilities.createHBoxToSelectFile(primaryStage, labelTextSelectExcludedFileList, labelTextWithExplanationSelectExcludedFileList);
        hbox.setSpacing(10);
        hbox.getChildren().add(new Pane()); // Placeholder content
        section.getChildren().add(hbox);

        hbox = Utilities.createHBoxToSelectFolder(primaryStage, labelTexSelectedExcludedPathList, labelTexSelectedWithExplanationExcludedPathList);
        hbox.setSpacing(10);
        hbox.getChildren().add(new Pane()); // Placeholder content
        section.getChildren().add(hbox);

        return section;
        
	}
	
}
