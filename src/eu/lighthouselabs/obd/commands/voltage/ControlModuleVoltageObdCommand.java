/*
 * TODO put header
 */
package eu.lighthouselabs.obd.commands.voltage;

import eu.lighthouselabs.obd.commands.ObdCommand;
import eu.lighthouselabs.obd.enums.AvailableCommandNames;

/**
 * TODO put description
 */
public class ControlModuleVoltageObdCommand extends ObdCommand {

	/**
	 * Default ctor.
	 */
	public ControlModuleVoltageObdCommand() {
		super("01 42");
	}

	/**
	 * Copy ctor.
	 * 
	 * @param other
	 */
	public ControlModuleVoltageObdCommand(ControlModuleVoltageObdCommand other) {
		super(other);
	}

	@Override
	public String getFormattedResult() {
		String res = getResult();

		if (!"NODATA".equals(res)&&buffer!=null&&buffer.size()>3) {
			// ignore first two bytes [01 0C] of the response
			int a = buffer.get(2);
			int b = buffer.get(3);
			float value = a * 256 + b;
			
			// determine time
			res = String.format("%02f", value / 1000f);
		}

		return res;
	}

	@Override
	public String getName() {
		return AvailableCommandNames.CONTROL_MODULE_V.getValue();
	}
	
	@Override
	public AvailableCommandNames getId() {
		return AvailableCommandNames.CONTROL_MODULE_V;
	}
	
	public float getValueResult() {
		String res = getResult();
		float result = 0;
		if (!"NODATA".equals(res)&&buffer!=null&&buffer.size()>3) {
			// ignore first two bytes [01 0C] of the response
			int a = buffer.get(2);
			int b = buffer.get(3);
			float value = a * 256 + b;
			
			// determine time
			result =value / 1000f;
		}

		return result;
	}
}