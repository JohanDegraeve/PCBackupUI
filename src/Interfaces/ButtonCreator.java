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