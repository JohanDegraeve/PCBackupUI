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

/**
 * a parent class for AFileWithLastModified and AFolderWithFullPath<br>
 * An instance of AFileOrAFolderForFullPath is either a AFileWithLastModified or a AFolderWithFullPath which means it's a file or a folder<br>
 * 
 */
public abstract class AFileOrAFolderForFullPath {

	public AFileOrAFolderForFullPath() {
	}
	
}
