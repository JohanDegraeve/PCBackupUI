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
   
# Usage

usage should be clear when opening the UI

![image 1](https://github.com/JohanDegraeve/PCBackupUI/assets/13840461/43d96245-6a88-4b42-b9af-f2854b7a0744)

For the moment only in Dutch

# Compile

To build in Eclipse, following jar libraries are required (latest versions)
- Jackson Annotations: https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-annotations
- Jackson databind: https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
- Jackson core: https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-core

Java FX is downloaded here (use the correct operating system and architecture and download the SDK): https://gluonhq.com/products/javafx/

Extract the zip and copy all jars to a lib folder

Add all jars (10 in total) in the (to create after cloning the project) lib folder

Configure the build path and add all jars to "Modulepath", it should look like this
![image 2](https://github.com/JohanDegraeve/PCBackupUI/assets/13840461/d7d5e15d-ff7a-47b9-9783-b39bb1094741)

Export as jar runnable to PCBackupUI.jar

# Run in Eclipse

Strange but I first need to build the jar, store in somewhere (in my case it's in the Downloads folder) and add following in VM arguments of the run configuration ...

--module-path /Users/johandegraeve/Downloads/javafx-sdk-22/lib --add-modules javafx.controls,javafx.fxml -jar /Users/johandegraeve/Downloads/PCBackupUI.jar

Then I can run and debug.

# Run on Windows or Mac OSX

install java JRE

Additional files are needed for JavaFX, 
- for Windows: they are here in this repository: https://github.com/JohanDegraeve/PCBackupUI/tree/main/javafx-sdk-22.0.1
- for Mac: they are here https://gluonhq.com/products/javafx/ in the lib folder

Download and note the folder (probably Downloads folder)

Add the jars that were downloaded here https://gluonhq.com/products/javafx/, to the lib folder in javafx-sdk-22.0.1

In terminal or command window, here assuming javafx sdk libraries are in /Users/johandegraeve/Downloads/javafx-sdk-22.0.1/lib and PCBackupUI.jar is in the current folder


java --module-path /Users/johandegraeve/Downloads/javafx-sdk-22.0.1/lib --add-modules javafx.controls,javafx.fxml -jar PCBackupUI.jar



