package main;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

@FunctionalInterface
interface ActionHandler {
	/**
	 * if action is null then no checkbox is selected
	 * @param action
	 */
    void handleAction(Action action);
}

@FunctionalInterface 
interface TextFieldChanged { // changes in a textfield like file selection, 
	/**
	 * handleChange called either when user selected a file or folder with chooser, or when user typed a character, or removed characters
	 * @param newText is the current text in the textfield, null if empty
	 */
	void handleChange(String newText);
	
}

@FunctionalInterface
interface ButtonCreator {
    Button createButton(String buttonTextString, Stage stage, TextField textField, TextFieldChanged textFieldChanged);
}

@FunctionalInterface
interface ProcessText {
	void process(String text);
}

