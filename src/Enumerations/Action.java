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
package Enumerations;

public enum Action {
	
	FULLBACKUP("FULLBACKUP"), 
	INCREMENTALBACKUP("INCREMENTALBACKUP"), 
	RESTORE("RESTORE"), 
	SEARCH("SEARCH"),
	CLEANBACKUP("CLEANBACKUP");
	
	private final String stringValue;

	Action(String stringValue) {
        this.stringValue = stringValue;
    }

	public String getStringValue() {
        return stringValue;
    }
	
	/**
	 * creates action for given string value that represents the action. If now matching action found then return value is null
	 */
	public static Action stringToEnum(String value) {
		
		if (value == null) {return null;}
		
        for (Action myAction : Action.values()) {
            if (myAction.stringValue.equals(value)) {
                return myAction;
            }
        }
        
        return null;
        
    }
}
