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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import model.CommandLineArguments;

public class Logger {

	static BufferedWriter staticwriter = null;

	/**
	 * write texttolog to :<br>
	 * - System.out.println
	 * - to file
	 * @param texttoLog
	 */
	public static void log(String texttoLog) {
		
		// always log to System.out
		System.out.println(texttoLog);
		
		if (CommandLineArguments.logFilePathAsPath != null) {
			
			try {
				
				if (staticwriter == null) {
					staticwriter = new BufferedWriter(new FileWriter(CommandLineArguments.logFilePathAsPath.toString()));
				}
				
				staticwriter.write(texttoLog + "\n");
				staticwriter.flush();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
		}
		
		
	}
}
