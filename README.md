# PCBackupUI
Java app to Backup and Restore, full and incremental.

Also allows to search in backups.

# Backup
- Full-backup copies all source files and source folders to another place/disk and creates the file folderlist.json
  - The app creates a subfolder in the destination, example "2024-01-19 00;03;00 (Full)" which indicates the date and time the backup was taken
- for each backup (full or incremental), a json file is created in the root folder of the backup named folderlist.json, (example : https://github.com/JohanDegraeve/PCBackup/blob/main/folderlist.json)
  - The provided JSON file represents a hierarchical structure of folders and files, serving as a backup configuration. At the top level, the "type" field indicates that it is a folder or a file, denoted by "afolder" or "afile". Each instance has a "name" and "pathToBackup" field, representing the name of the folder and the backup path, respectively. The "fileOrFolderList" field contains an array of objects, representing either files and/or nested folders.
  - Each file object includes "name," "pathToBackup," and "ts" (timestamp) fields. The "name" field signifies the file's name, "pathToBackup" indicates the backup path, and "ts" represents the timestamp of the file.
  - "pathToBackup" is just a subfoldername, example "2024-01-19 00;26;57 (Incremental)" and tells us where, in which previous full or incremental bakcup, the latest version of the file is stored.
  - there's another json file creaeted, "folderlist-withfullpaths.json". This is a the similar to "folderlist.json", but the timestamps are in human readable format and it includes the full path of a file. A handy tool to search in json files is "Dadroit json viewer". 
- Incremental Backup
  - creates a new folder where the incremental backup is stored, example "2024-01-19 00;26;57 (Incremental)"
  - Each time an incremental backup is taken, then first the file folderlist.json from the previous backup (either full or incremental) is copied and parsed.
  - then the json structure is created for the current status of the source, ie the app will go through the current files and folders, read the names of the files and folders and the timestamps of the files
  - A comparison is made of the two json structures
      - when a file is removed in the source, it is removed also in the destination json structure
      - when a file is added in the source, it is added also in the destination json structure, and also copied to the destination folder. The json structure field pathToBackup will have the name of the new incremental backup
      - when a file is modified in the source (meaning has a more recent modified timestamp than the one in the latest backup), then the same as done as for new files
      - when a folder is added in the source, it is added also in the destination json structure, but not necessarily created in the destination, not if it has no files
      - when a folder is removed in the source, it is removed in the destination json structure

     
# Restore
- Browse through the list of bacups, sorted by date and time and select the backupfolder
- Then browse through the folderstructure and select the folder to restore. You can only select folders, not individual files. The files are listed but not individually selectable.
- Define the path where to restore to

# Search
Search allows to find 1, 2 or 3 strings (texts) in the file or folder names. A csv file is generated (',' is used as delimiter) with the search results.
You can specify the start and search date of the backups to search in. It will give the latest version of a file that contains the search texts.
   
# Usage

usage should be clear when opening the UI

![image 1](https://github.com/JohanDegraeve/PCBackupUI/assets/13840461/43d96245-6a88-4b42-b9af-f2854b7a0744)

For the moment only in Dutch

# Setup in Eclipse

## Required Libraries

### Jackson Libraries (version 2.17.0 or higher)
Download the following JAR files from Maven Central:
- Jackson Annotations: https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-annotations/2.17.0/jackson-annotations-2.17.0.jar
- Jackson Databind: https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-databind/2.17.0/jackson-databind-2.17.0.jar
- Jackson Core: https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-core/2.17.0/jackson-core-2.17.0.jar

### JavaFX SDK (version 22 or higher)
Download JavaFX SDK for your operating system from: https://gluonhq.com/products/javafx/

**Important:** Download the correct version for your OS:
- Windows: Download the Windows SDK
- macOS: Download the macOS SDK
- Linux: Download the Linux SDK

Extract the JavaFX SDK to a location on your system (e.g., `C:\javafx-sdk-25.0.2` on Windows).

## Eclipse Configuration

### 1. Add Libraries to Build Path
1. Right-click on your project → Properties → Java Build Path → Libraries tab
2. Click "Add External JARs..."
3. Add the three Jackson JAR files
4. Add all JavaFX JAR files from the `lib` folder of your JavaFX SDK installation
5. **Important:** Make sure all JARs are added to the **Classpath**, NOT the Modulepath
6. Click Apply and Close

### 2. Configure JDK
1. Window → Preferences → Java → Installed JREs
2. Make sure you have JDK 21 or higher installed and selected
3. For your project: Right-click → Properties → Java Build Path → Libraries
4. Edit the JRE System Library to use JDK 21 or higher

### 3. Configure Run Configuration
1. Right-click on your project → Run As → Run Configurations...
2. Select your Main class (main.Main)
3. Go to the **Arguments** tab
4. In **VM arguments**, add (adjust the path to your JavaFX SDK location):

**Windows:**
```
--module-path "C:\javafx-sdk-25.0.2\lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base --enable-native-access=javafx.graphics
```

**macOS/Linux:**
```
--module-path "/path/to/javafx-sdk-25.0.2/lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base --enable-native-access=javafx.graphics
```

5. Click Apply → Run

The application should now start directly from Eclipse without needing to build a JAR first.

## Export as Runnable JAR (Optional)

If you want to create a standalone JAR file:
1. File → Export → Java → Runnable JAR file
2. Select your launch configuration
3. Choose export destination
4. Click Finish

# Run on Windows or Mac OSX

install java JRE

Additional files are needed for JavaFX, 
- for Windows: they are here in this repository: https://github.com/JohanDegraeve/PCBackupUI/tree/main/javafx-sdk-22.0.1
- for Mac: they are here https://gluonhq.com/products/javafx/ in the lib folder

Download and note the folder (probably Downloads folder)

Add the jars that were downloaded here https://gluonhq.com/products/javafx/, to the lib folder in javafx-sdk-22.0.1

In terminal or command window, here assuming javafx sdk libraries are in /Users/johandegraeve/Downloads/javafx-sdk-22.0.1/lib and PCBackupUI.jar is in the current folder


java --module-path /Users/johandegraeve/Downloads/javafx-sdk-22.0.1/lib --add-modules javafx.controls,javafx.fxml -jar PCBackupUI.jar



