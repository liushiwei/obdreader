package com.george.obdreader;

import eu.lighthouselabs.obd.commands.ObdCommand;
import eu.lighthouselabs.obd.enums.AvailableCommandNames;

public class OBDCommand extends ObdCommand {

	private OBDEnums command;

	private float A, B, C, D, E;
	
	public static final int NORESULT = -0xfffffff;

	public OBDCommand(OBDEnums command) {
		super(command.getCommand());
		this.command = command;
	}

	@Override
	public String getFormattedResult() {
		// TODO Auto-generated method stub
		return  String.format("%f%s", getValue(), command.getUnit());
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return command.getCommand();
	}

	@Override
	public AvailableCommandNames getId() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public OBDEnums getEnums(){
		return command;
	}

	public float getValue() {
		float result = NORESULT;
		if (rawData != null) {
			if (rawData.contains("SEARCHING") || rawData.contains("DATA")|| rawData.contains("STOP")) {
				rawData = "NODATA";
			}else{
				//buffer.clear();
				
				// read string each two chars
//				int begin = 0;
//				int end = 2;
//				try{
//				while (end <= rawData.length()) {
//					String temp = "0x" + rawData.substring(begin, end);
//					buffer.add(Integer.decode(temp));
//					begin = end;
//					end += 2;
//				}
//				}catch(NumberFormatException e){
//					//do nothing;
//				}
				if(buffer.size()==command.getResultSize()+2){
					switch(command.getResultSize()){
					case 5:
						E = buffer.get(6);
					case 4:
						D = buffer.get(5);
					case 3:
						C = buffer.get(4);
					case 2:
						B = buffer.get(3);
					case 1:
						A = buffer.get(2);
					}
				}
				return valueFormula();
			}
		}
		return result;
	}
	
	private float valueFormula(){
		switch(command.getFormula()){
		case 1:
			return A*100/255;
		case 2:
			return A-40;
		case 3:
			return (A-128) * 100/128;
		case 4:
			return A*3;
		case 5:
			return A;
		case 6:
			return ((A*256)+B)/4;
		case 7:
			return A/2 - 64;
		case 8:
			return A/200;
		case 9:
			return (B-128) * 100/128 ;
		case 10:
			return (A*256)+B ;
		case 11:
			return (float) (((A*256)+B) * 0.079) ;
		case 12:
			return ((A*256)+B) * 10 ;
		case 13:
			return ((A*256)+B)*2/65535 ;
		case 14:
			return ((C*256)+D)*8/65535;
		case 15:
			return ((A*256)+B) * 10 ;
		case 16:
			return ((A*256)+B) / 100;
		case 17:
			return ((A*256)+B)/4;
		case 18:
			return ((C*256)+D)/256 - 128;
		case 19:
			return ((A*256)+B)/10 - 40;
		case 20:
			return ((A*256)+B)/1000;
		case 21:
			return ((A*256)+B)*100/255;
		case 22:
			return ((A*256)+B)/32768;
		case 23:
			return A*10;
		case 24:
			return ((A*256)+B)/200;
		case 25:
			return ((A*256)+B)-32767;
		case 26:
			return (A-128)*100/128;
		case 27:
			return (((A*256)+B)-26880)/128;
		case 28:
			return (float) (((A*256)+B)*0.05);
		case 29:
			return A-125;
		}
		return -1;
	}

}
