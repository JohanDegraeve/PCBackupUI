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

import java.util.ArrayList;
import java.util.List;

public class AFolderWithFullPath extends AFileOrAFolderForFullPath {

	/**
	 * the path: it will have the full path only if it contains at least one file, otherwise just the subfolder name<br>
	 */
	private String path;

	/**
	 * the list of instances of AFileOrAFolder
	 */
	private List<AFileOrAFolderForFullPath> fileOrFolderList;

	public AFolderWithFullPath(String path, String pathToBackup) {
		
		this.path = path;
		this.fileOrFolderList = new ArrayList<>();
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public List<AFileOrAFolderForFullPath> getFileOrFolderList() {
		return fileOrFolderList;
	}

	public void setFileOrFolderList(List<AFileOrAFolderForFullPath> fileOrFolderList) {
		this.fileOrFolderList = fileOrFolderList;
	}
	
}
