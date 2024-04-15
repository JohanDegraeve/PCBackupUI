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

public class AFileWithLastModified extends AFileOrAFolderForFullPath {

	/**
	 * the name of the file
	 */
	private String name;
	
	/**
	 * the timestamp the file was created or modified, in human readable, local format<br>
	 */
	private String ts = null;

	/**
	 * in which backup folder can we find the latest version of the file<br>
	 * It's a path relative to the source folder of the backup.
	 */
	private String pathToBackup;

	public AFileWithLastModified(String name, String pathToBackup) {
		
		this.name = name; 
		this.pathToBackup = pathToBackup;
		
	}

	public String getts() {
		return ts;
	}
	
	public void setts(String ts) {
		this.ts = ts;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getPathToBackup() {
		return pathToBackup;
	}

	public void setPathToBackup(String pathToBackup) {
		this.pathToBackup = pathToBackup;
	}

}
