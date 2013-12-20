package com.george.obdreader;

import eu.lighthouselabs.obd.commands.ObdCommand;
import eu.lighthouselabs.obd.enums.AvailableCommandNames;

public class OBDCommand extends ObdCommand {

	private OBDEnums command;

	private int A, B, C, D, E;
	
	public static final int NORESULT = -0xffffffff;

	public OBDCommand(OBDEnums command) {
		super(command.getCommand());
		this.command = command;
	}

	@Override
	public String getFormattedResult() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AvailableCommandNames getId() {
		// TODO Auto-generated method stub
		return null;
	}

	public float getValue() {
		float result = NORESULT;
		if (rawData != null) {
			if (rawData.contains("SEARCHING") || rawData.contains("DATA")|| rawData.contains("STOP")) {
				rawData = "NODATA";
			}else{
				buffer.clear();
				
				// read string each two chars
				int begin = 0;
				int end = 2;
				try{
				while (end <= rawData.length()) {
					String temp = "0x" + rawData.substring(begin, end);
					buffer.add(Integer.decode(temp));
					begin = end;
					end += 2;
				}
				}catch(NumberFormatException e){
					//do nothing;
				}
			}
		}
		return 0;
	}

}
