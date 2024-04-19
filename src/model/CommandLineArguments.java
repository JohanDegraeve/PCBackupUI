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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import Interfaces.ProcessText;
import utilities.OtherUtilities;

/**
 * all the needed arguments, like Source folder, destination folder, ...
 * 
 */
public class CommandLineArguments {
	
    /**
     * for search, backups younger than this date will not be searched in
     */
    public Date startSearchDate = new Date(0);
    
    /**
     * for search, backups younger than this date will not be searched in
     */
    public Date endSearchDate = new Date();
    
    /**
	 * Folder where the actual source that we're backing up is stored<br>
	 * This is the full path, example E:\sourcefolder\
	 */
    public String source;
    
    /**
     * Folder where we store the backups, either full or incremental.<br>
     * Each time we create a new backup, a subfolder will be created in that folder 
     */
    public String destination;

    /**
     * in case restore is used, foldername where date should be restored to
     */
    public String restoreto;
    
    /**
     * If true, full backup, if false, incremental backup
     */
    public boolean fullBackup = false;
    
    /**
     * is it a backup  or a restore
     */
    public boolean backup = true;
    
    /**
     * user will search through the backups, if true then backup value is ignored
     */
    public boolean search = false;
    
    /**
     * path for logging, this includes the filename
     */
    public static Path logFilePathAsPath = null;
    
    /**
     * filenames that should be ignored, ie not added to the folderlist.json and not copied in backups<br>
     */
    public List<String> excludedFiles = new ArrayList<>();
    
    /**
     * paths that should be ignored, ie not added to the folderlist.json and not copied in backups<br>
     * These are full paths, starting from the main folder
     */
    public List<String> excludedPaths = new ArrayList<>();
    
    /**
     * only for restore, Date for which restore needs to be done
     */
    public Date restoreDate = null;
    
    /**
     * used in case of restore, can be an empty string<br>
     * not null<br>
     * Specifies the subfolder within the source to restore 
     */
    public String subfolderToRestore = "";
    
    /**
     * used to store foldername mappings folderNameMapping
     */
    public HashMap<String, String> folderNameMapping = new HashMap<>();
    
    /**
     * when restoring, should copy be done in overwrite mode or not
     */
    public boolean overwrite = false;
    
    /**
     * folder to store search results
     */
    public String writesearchto = null;
   
    /**
     * regex pattern to use in search
     */
	public Pattern searchTextPattern = null;
	
	/**
	 * for testing only
	 */
	public boolean addpathlengthforallfolders = false;
	
	/**
	 * for testing only
	 */
	public boolean addpathlengthforfolderswithnewormodifiedcontent = false;
	
	/**
	 * is called with log text, this is an interface defined by process that created CommandLineArguments
	 */
	public ProcessText processText;

	/**
	 * constructor
	 * @param processText logging info will be pass with processText
	 */
	public CommandLineArguments(Date startSearchDate, Date endSearchDate, String source, String destination,
			String restoreto, boolean fullBackup, boolean backup, boolean search, String logfilefolder,
			String excludedFiles, String excludedPaths, Date restoreDate, String subfolderToRestore,
			String folderNameMapping, boolean overwrite, String writesearchto,
			Pattern searchTextPattern, boolean addpathlengthforallfolders,
			boolean addpathlengthforfolderswithnewormodifiedcontent, String searchText, ProcessText processText) {
		super();
		this.startSearchDate = startSearchDate;
		this.endSearchDate = endSearchDate;
		this.source = source;
		this.destination = destination;
		this.restoreto = restoreto;
		this.fullBackup = fullBackup;
		this.backup = backup;
		this.search = search;
		this.excludedFiles = OtherUtilities.getListFromFileList(excludedFiles, processText);
		this.excludedPaths = OtherUtilities.getListFromFileList(excludedPaths, processText);
		this.restoreDate = restoreDate;
		this.subfolderToRestore = subfolderToRestore;
		this.folderNameMapping = OtherUtilities.readKeyValuePairFromFile(folderNameMapping, processText);
		this.overwrite = overwrite;
		this.writesearchto = writesearchto;
		this.searchTextPattern = searchTextPattern;
		this.addpathlengthforallfolders = addpathlengthforallfolders;
		this.addpathlengthforfolderswithnewormodifiedcontent = addpathlengthforfolderswithnewormodifiedcontent;
		this.processText = processText;
		
		configureLogFile(logfilefolder, processText);
		
	}

    private static void configureLogFile(String logFilePath, ProcessText processText) {
    	
    	// create filename
    	String logfileNameString = "PCBackup-" + OtherUtilities.dateToString(new Date(), Constants.LOGFILEDATEFORMAT_STRING) + ".log";
    	
    	// create the Path
    	logFilePathAsPath = Paths.get(logFilePath, logfileNameString);
    	
        processText.process("Logging to file " + logFilePathAsPath);
        
    }
        
}
