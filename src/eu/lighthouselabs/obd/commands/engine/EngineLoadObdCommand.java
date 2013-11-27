/*
 * TODO put header
 */
package eu.lighthouselabs.obd.commands.engine;

import eu.lighthouselabs.obd.commands.ObdCommand;
import eu.lighthouselabs.obd.commands.PercentageObdCommand;
import eu.lighthouselabs.obd.enums.AvailableCommandNames;

/**
 * Calculated Engine Load value.
 */
public class EngineLoadObdCommand extends PercentageObdCommand {

	/**
	 * @param command
	 */
	public EngineLoadObdCommand() {
		super("01 04");
	}

	/**
	 * @param other
	 */
	public EngineLoadObdCommand(ObdCommand other) {
		super(other);
	}

	/* (non-Javadoc)
	 * @see eu.lighthouselabs.obd.commands.ObdCommand#getName()
	 */
	@Override
	public String getName() {
		return AvailableCommandNames.ENGINE_LOAD.getValue();
	}
	
	@Override
	public AvailableCommandNames getId() {
		return AvailableCommandNames.ENGINE_LOAD;
	}
	
	@Override
	public String getFormattedResult() {
		String res = getResult();

		if (!"NODATA".equals(res) &&buffer!=null&& buffer.size()>2) {
			// ignore first two bytes [hh hh] of the response
			float tempValue = (buffer.get(2) * 100.0f) / 255.0f;
			res = String.format("%.1f%s", tempValue, "%");
		}

		return res;
	}
	
	public int getValueResult() {
		String res = getResult();
		int result = 0;
		if (!"NODATA".equals(res) &&buffer!=null&& buffer.size()>2) {
			// ignore first two bytes [hh hh] of the response
			result = (int) ((buffer.get(2) * 100.0f) / 255.0f);
		}

		return result;
	}

}