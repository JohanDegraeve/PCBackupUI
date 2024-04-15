package Interfaces;

import Enumerations.Action;

@FunctionalInterface
public interface ActionHandler {
	/**
	 * if action is null then no checkbox is selected
	 * @param action
	 */
    void handleAction(Action action);
}
