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
import java.nio.channels.spi.AbstractInterruptibleChannel;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class Main extends Application {

    VBox root = new VBox();
    Stage primaryStage;
    VBox sectionBackupParametersBox;

    @Override
    public void start(Stage primaryStage) {
    	
    	this.primaryStage = primaryStage;

    	int sceneWidth = 800;
    	int sceneHeight = 400;

    	// Create a VBox to hold all sections
        root.setPadding(new Insets(10));
        root.setSpacing(10);

        // Create the first section
        VBox section1 = Section1.createSection1(primaryStage);
        root.getChildren().add(section1);

        // Create a divider line between sections
        Line divider1 = createDivider(sceneWidth);
        root.getChildren().add(divider1);

        // section2 is where user selects the action : full backup, incremental backup, restore or search
        VBox section2 = Section2.createSection2(primaryStage, (action) -> addAndRemoveSection(action) );
        root.getChildren().add(section2);
        
        // section backupParameters
        sectionBackupParametersBox = SectionBackupParameters.createSectionBackupParameters(primaryStage);

        // Create a scene with the VBox and set it on the primary stage
        Scene scene = new Scene(root, sceneWidth, sceneHeight);
        primaryStage.setScene(scene);

        // Show the primary stage
        primaryStage.show();
        
    }

    private void runExistingApplication(String arguments) {
        try {
            // Replace "java" with the path to your Java executable if not in the system path
            // Replace "ExistingApplication.jar" with the name of your existing Java application's JAR file
            //Runtime.getRuntime().exec("java -jar /Users/johandegraeve/Downloads/PCBackup.jar " + arguments);
        } catch (Exception ex) {
            ex.printStackTrace();
            // Handle exception
        }
    }
    
    @FunctionalInterface
    interface ActionHandler {
        void handleAction(Action action);
    }
    
    /**
     * function that adds and or removes sections, depending on value of action
     * @param action
     */
    private void addAndRemoveSection(Action action) {
    	
    	switch (action) {
		case FULLBACKUP: {
			
			root.getChildren().add(sectionBackupParametersBox);
			break;
			
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + action);
		}
    	
    }
    
    private Line createDivider(int width) {
        Line divider = new Line();
        divider.setStartX(0);
        divider.setStartY(0);
        divider.setEndX(width - 20); // Adjust the length as needed
        divider.setEndY(0);
        divider.setStrokeWidth(1); // Adjust the thickness as needed
        divider.setStyle("-fx-stroke: black;"); // Adjust the color as needed
        return divider;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
