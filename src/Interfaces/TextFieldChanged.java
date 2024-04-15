package Interfaces;

@FunctionalInterface
public interface TextFieldChanged { // changes in a textfield like file selection, 
	/**
	 * handleChange called either when user selected a file or folder with chooser, or when user typed a character, or removed characters
	 * @param newText is the current text in the textfield, null if empty
	 */
	void handleChange(String newText);
	
}