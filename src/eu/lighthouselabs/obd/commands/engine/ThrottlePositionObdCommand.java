/*
 * TODO put header
 */
package eu.lighthouselabs.obd.commands.engine;

import eu.lighthouselabs.obd.commands.PercentageObdCommand;
import eu.lighthouselabs.obd.enums.AvailableCommandNames;

/**
 * Read the throttle position in percentage.
 */
public class ThrottlePositionObdCommand extends PercentageObdCommand {

	/**
	 * Default ctor.
	 */
	public ThrottlePositionObdCommand() {
		super("01 11");
	}

	/**
	 * Copy ctor.
	 * 
	 * @param other
	 */
	public ThrottlePositionObdCommand(ThrottlePositionObdCommand other) {
		super(other);
	}

	/**
	 * 
	 */
	@Override
	public String getName() {
		return AvailableCommandNames.THROTTLE_POS.getValue();
	}
	
	@Override
	public AvailableCommandNames getId() {
		return AvailableCommandNames.THROTTLE_POS;
	}
	
	public int getValueResult(){
		String res = getResult();
		int result = 0;
		if (!"NODATA".equals(res)&&buffer!=null&&buffer.size()>2) {
			result = (int) ((buffer.get(2) * 100.0f) / 255.0f);
		}
		return result;
	}
	
}