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
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {

    	//////// CONSTANTS for sizes, positions etc.
    	int rowOfSourceAndDest = 0;
    	int sceneWidth = 800;
    	int sceneHeight = 100;
    	
    	//////// THE TITLE
        primaryStage.setTitle("Command Line Arguments GUI");

        
        //////// THE GRID
        
        // Padding is the space between the edges of the grid and its children. 
        // The Insets class specifies the padding from the top, right, bottom, and left edges respectively. 
        // In this case, it sets 10 pixels of padding on all sides.
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        
        // This line sets the vertical gap between the rows of the grid. It specifies the amount of space between adjacent rows in pixels. 
        // In this case, it sets the vertical gap to 5 pixels.
        grid.setVgap(5);
        
        // This line sets the horizontal gap between the columns of the grid. Similar to setVgap(), 
        // it specifies the amount of space between adjacent columns in pixels. Here, 
        // it sets the horizontal gap to 5 pixels.
        grid.setHgap(5);
        
        //////// THE SOURCE
        HBox hBox = Section1.createSectionWithSourceAndDest(grid, primaryStage, rowOfSourceAndDest);
        grid.getChildren().addAll(hBox);

        Scene scene = new Scene(grid, sceneWidth, sceneHeight);
        primaryStage.setScene(scene);
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

    public static void main(String[] args) {
        launch(args);
    }
}
