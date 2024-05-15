# PCBackupUI
PCBackup User Interface
PCBackup is in this repository: https://github.com/JohanDegraeve/PCBackup

PCBackupUI is a user interface on top of PCBackup

![image 1](https://github.com/JohanDegraeve/PCBackupUI/assets/13840461/43d96245-6a88-4b42-b9af-f2854b7a0744)

For the moment only in Dutch

# Usage

usage should be clear when opening the UI

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

Strange but following needs to be in VM arguments of the run configuration ...

--module-path /Users/johandegraeve/Downloads/javafx-sdk-22/lib --add-modules javafx.controls,javafx.fxml -jar /Users/johandegraeve/Downloads/PCBackupUI.jar

# Run on Windows or Mac OSX

install java JRE

In terminal or command window

java --module-path java-sdk-22-folder/lib --add-modules javafx.controls,javafx.fxml -jar PCBackupUI.jar

with java-sdk-22-folder/lib the folder that has the javafx jars 


