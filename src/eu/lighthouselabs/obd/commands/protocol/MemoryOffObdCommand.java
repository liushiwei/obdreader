/*
 * TODO put header
 */
package eu.lighthouselabs.obd.commands.protocol;

import eu.lighthouselabs.obd.commands.ObdCommand;

/**
 * This command will turn-off echo.
 */
public class MemoryOffObdCommand extends ObdCommand {

	/**
	 * @param command
	 */
	public MemoryOffObdCommand() {
		super("AT M0");
	}

	/**
	 * @param other
	 */
	public MemoryOffObdCommand(ObdCommand other) {
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
		return "Memory Off";
	}

}