package Interfaces;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

@FunctionalInterface
public interface ButtonCreator {
	/**
	 * used where a user can select a folder or file, either by literally typing in a textfield or by selecting a button
	 * @param buttonTextString
	 * @param stage
	 * @param textField
	 * @param textFieldChanged
	 * @return
	 */
    Button createButton(String buttonTextString, Stage stage, TextField textField, TextFieldChanged textFieldChanged);
}