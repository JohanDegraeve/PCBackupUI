/*
 * Copyright 2024 Johan Degraeve
 *
 * This file is part of PCBackup.
 *
 * PCBackup is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PCBackup is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PCBackup. If not, see <https://www.gnu.org/licenses/>.
 */
package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

//AFileOrAFolder class
@JsonTypeInfo(
     use = JsonTypeInfo.Id.NAME,
     include = JsonTypeInfo.As.PROPERTY,
     property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = AFile.class, name = "afile"),
    @JsonSubTypes.Type(value = AFolder.class, name = "afolder")
})
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AFileOrAFolder {

	/**
	 * name of the file or folder
	 */
	private String name;
	
	/**
	 * in which backup folder can we find the latest version of the file<br>
	 * It's a path relative to the source folder of the backup. Meaning it doesn't include the source folder name, and it doesn't start with a slash<br>
	 * It's something like '2023-12-06 18;24;41 (Full)' or '2023-12-28 17;07;13 (Incremental)'
	 */
	private String pathToBackup;

	public AFileOrAFolder(String name, String pathToBackup) {
		if (name == null) {throw new IllegalArgumentException("in constructor AFileOrAFolder, Name cannot be null");}
		if (pathToBackup == null) {throw new IllegalArgumentException("in constructor AFileOrAFolder, pathToBackup cannot be null");}
		this.name = name;
		this.pathToBackup = pathToBackup;
	}
	
	/**
	 * created to allow json deserialisation
	 */
	public AFileOrAFolder() {
		// TODO Auto-generated constructor stub
	}

	public String getPathToBackup() {
		return pathToBackup;
	}

	public void setPathToBackup(String pathToBackup) {
		this.pathToBackup = pathToBackup;
	}

	/**
	 * getter for name of the file or folder
	 * 
	 * @return the name of the file or the folder
	 */
    public String getName() {
        return name;
    }

    /**
     * setter for the name of the file or folder
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

}
