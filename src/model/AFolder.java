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

import java.util.List;

import java.util.ArrayList;

//AFileOrAFolder class
public class AFolder extends AFileOrAFolder {

	/**
	 * the list of instance of AFileOrAFolder
	 */
	private List<AFileOrAFolder> fileOrFolderList;
	
	/**
	 * created to allow json deserialisation
	 */
	public AFolder() {
		super();
	}
	
	public AFolder(String name, String pathToBackup) {
		
		super(name, pathToBackup);
		// Initialize the attribute with an empty list
        this.fileOrFolderList = new ArrayList<>();
        
	}

	/**
	 * @return the fileOrFolderList
	 */
	public List<AFileOrAFolder> getFileOrFolderList() {
		return fileOrFolderList;
	}

	/**
	 * Method to add a file or folder to the list 
	 * @param fileOrFolder
	 */
    public void addFileOrFolder(AFileOrAFolder fileOrFolder) {
        fileOrFolderList.add(fileOrFolder);
    }
    
}
