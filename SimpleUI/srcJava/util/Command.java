package util;

public interface Command {
	/**
	 * @return true if the command was executed correctly
	 */
	public abstract boolean execute();
}
