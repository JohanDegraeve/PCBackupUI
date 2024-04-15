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

import java.nio.file.Path;
import java.util.List;

public class PathUtilities {

	public static Path concatenatePaths(Path basePath, List<String> subfolders) {
        // Start with the base path
        Path resultPath = basePath;

        // Iterate through the subfolders and resolve each one
        for (String subfolder : subfolders) {
            resultPath = resultPath.resolve(subfolder);
        }

        return resultPath;
    }	
	
	/**
	 * say Path is a relative path, example subfolder1/subfolder2/subfolder3<br>
	 * this function will split into an array of Paths, one per subfolder
	 * @param path
	 * @return
	 */
	public static Path[] splitPath(Path path) {
        // Get the number of elements in the path
        int count = path.getNameCount();

        // Create an array of Path objects
        Path[] subfolders = new Path[count];

        // Iterate through the path's elements and store them in the array
        for (int i = 0; i < count; i++) {
            subfolders[i] = path.getName(i);
        }

        return subfolders;
    }
	
}
