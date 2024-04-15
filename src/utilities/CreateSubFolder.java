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
package utilities;

import java.io.File;
import java.nio.file.Path;

public class CreateSubFolder {

	public static Path createSubFolder(String parentFolderPath, String subfolderName) {

	    File parentFolder = new File(parentFolderPath);
	    

	    // Check if the parent folder exists
	    if (!parentFolder.exists()) {
	        throw new IllegalStateException("Parent folder does not exist: " + parentFolderPath);
	    }

	    // Create the subfolder
	    File subfolder = new File(parentFolder, subfolderName);

	    if (!subfolder.mkdir()) {
	    	Logger.log("Subfolder already exists or creation failed: " + subfolder.getAbsolutePath());
	    }
	    
	    return subfolder.toPath();
	    		
	 }
    
}
