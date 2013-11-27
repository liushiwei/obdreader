/*
 * TODO put header
 */
package eu.lighthouselabs.obd.commands.control;

import eu.lighthouselabs.obd.commands.PercentageObdCommand;
import eu.lighthouselabs.obd.enums.AvailableCommandNames;

/**
 * TODO put description
 * 
 * Timing Advance
 */
public class TimingAdvanceObdCommand extends PercentageObdCommand {

	public TimingAdvanceObdCommand() {
		super("01 0E");
	}

	public TimingAdvanceObdCommand(TimingAdvanceObdCommand other) {
		super(other);
	}

	@Override
	public String getName() {
		return AvailableCommandNames.TIMING_ADVANCE.getValue();
	}
	
	@Override
	public AvailableCommandNames getId() {
		return AvailableCommandNames.TIMING_ADVANCE;
	}
	
	public int getValueResult(){
		String res = getResult();
		int result = 0;
		if (!"NODATA".equals(res)&&buffer!=null&&buffer.size()>2) {
			// ignore first two bytes [hh hh] of the response
			result = (int) (buffer.get(2)/ 2f-64);
		}
		return result;
	}
}