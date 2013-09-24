/*
 * TODO put header
 */
package eu.lighthouselabs.obd.commands.protocol;

import eu.lighthouselabs.obd.commands.ObdCommand;

/**
 * This command will turn-off echo.
 */
public class SimpleObdCommand extends ObdCommand {
	
	private String name;

	/**
	 * @param command
	 */
	public SimpleObdCommand(String cmd ,String name) {
		super(cmd);
		this.name = name;
	}

	/**
	 * @param other
	 */
	public SimpleObdCommand(ObdCommand other) {
		super(other);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.lighthouselabs.obd.commands.ObdCommand#getFormattedResult()
	 */
	@Override
	public String getFormattedResult() {
		return getResult();
	}

	@Override
	public String getName() {
		return name;
	}

}