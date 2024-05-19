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

@FunctionalInterface
public interface TextFieldChanged { // changes in a textfield like file selection, 
	/**
	 * handleChange called either when user selected a file or folder with chooser, or when user typed a character, or removed characters
	 * @param newText is the current text in the textfield, null if empty
	 */
	void handleChange(String newText);
	
}