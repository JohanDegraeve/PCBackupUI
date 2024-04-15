package Interfaces;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

@FunctionalInterface
public interface ButtonCreator {
    Button createButton(String buttonTextString, Stage stage, TextField textField, TextFieldChanged textFieldChanged);
}