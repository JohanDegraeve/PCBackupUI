package Enumerations;

public enum Action {
	
	FULLBACKUP("FULLBACKUP"), 
	INCREMENTALBACKUP("INCREMENTALBACKUP"), 
	RESTORE("RESTORE"), 
	SEARCH("SEARCH");
	
	private final String stringValue;

	Action(String stringValue) {
        this.stringValue = stringValue;
    }

	public String getStringValue() {
        return stringValue;
    }
	
	/**
	 * creates action for given string value that represents the action. If now matching action found then return value is null
	 */
	public static Action stringToEnum(String value) {
		
		if (value == null) {return null;}
		
        for (Action myAction : Action.values()) {
            if (myAction.stringValue.equals(value)) {
                return myAction;
            }
        }
        
        return null;
        
    }
}
