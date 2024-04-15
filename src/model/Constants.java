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

public class Constants {

	/**
	 * backup folder date format to be used to create full or incremental backup folder name<br>
	 * So this is what comes before the "full" or "incremental"
	 */
	public static final String BACKUPFOLDERDATEFORMAT_STRING = "yyyy-MM-dd HH;mm;ss";
	
	/**
	 * date format for dates in arguments
	 */
	public static final String ARGUMENTDATEFORMAT_STRING = "yyyy-MM-dd-HH-mm-ss";
	
	/**
	 * date format for printing to user
	 */
	public static final String OUTPUTDATEFORMAT_STRING = "yyyy MM dd HH:mm:ss";
	
	/**
	 * date format for the log file
	 */
	public static final String LOGFILEDATEFORMAT_STRING = "yyyy-MM-dd-HH-mm-ss";
	
}
