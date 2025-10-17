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

//AFileOrAFolder class
public class AFile extends AFileOrAFolder {

	/**
	 * when was the file last modified 
	 */
	private long ts;
	
	/**
	 * creates a file with lastmodifedTimeStamp (ts)
	 * @param name 
	 * @param ts lastmodified timestamp, created shorter to save bytes in the json representation
	 * @param pathToBackup
	 */
	public AFile(String name, long ts, String pathToBackup) {

		super(name, pathToBackup);
		
		if (pathToBackup == null) {throw new IllegalArgumentException("pathToIncrementalBackup cannot be null");}
		
		this.ts = ts;
	}

	/**
	 * created to allow json deserialisation
	 */
	public AFile() {
		super();
	}

	/**
	 * @return the lastmodifedTimeStamp ts
	 * 
	 */
	public long getts() {
		return ts;
	}

	/**
	 * @param ts the lastmodifedTimeStamp to set
	 */
	public void setts(long ts) {
		//if (ts == 0L) {throw new IllegalArgumentException("ts cannot be null");}
		this.ts = ts;
	}

}