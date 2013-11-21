/*
 * TODO put header
 */
package eu.lighthouselabs.obd.commands.control;

import java.util.ArrayList;

import eu.lighthouselabs.obd.commands.ObdCommand;
import eu.lighthouselabs.obd.enums.AvailableCommandNames;

/**
 * In order to get ECU Trouble Codes, one must first send a DtcNumberObdCommand
 * and by so, determining the number of error codes available by means of
 * getTotalAvailableCodes().
 * 
 * If none are available (totalCodes < 1), don't instantiate this command.
 */
public class PendingTroubleCodesObdCommand extends ObdCommand {

	protected final static char[] dtcLetters = { 'P', 'C', 'B', 'U' };

	private String codes[] = null;
	private int howManyTroubleCodes = 0;

	/**
	 * Default ctor.
	 */
	public PendingTroubleCodesObdCommand(int howManyTroubleCodes) {
		super("07");

		this.howManyTroubleCodes = howManyTroubleCodes;
		codes = new String[howManyTroubleCodes];
	}

	/**
	 * Copy ctor.
	 * 
	 * @param other
	 */
	public PendingTroubleCodesObdCommand(PendingTroubleCodesObdCommand other) {
		super(other);
		codes = new String[other.howManyTroubleCodes];
	}

	// TODO clean
	// int count = numCmd.getCodeCount();
	// int dtcNum = (count + 2) / 3;
	// for (int i = 0; i < dtcNum; i++) {
	// sendCommand(cmd);
	// String res = getResult();
	// for (int j = 0; j < 3; j++) {
	// String byte1 = res.substring(3 + j * 6, 5 + j * 6);
	// String byte2 = res.substring(6 + j * 6, 8 + j * 6);
	// int b1 = Integer.parseInt(byte1, 16);
	// int b2 = Integer.parseInt(byte2, 16);
	// int val = (b1 << 8) + b2;
	// if (val == 0) {
	// break;
	// }
	// String code = "P";
	// if ((val & 0xC000) > 14) {
	// code = "C";
	// }
	// code += Integer.toString((val & 0x3000) >> 12);
	// code += Integer.toString((val & 0x0fff));
	// codes.append(code);
	// codes.append("\n");
	// }

	/**
	 * @return the formatted result of this command in string representation.
	 */
	public String formatResult() {
		String res = getResult();

		if (!"NODATA".equals(res)) {
			res = res.replaceAll("\r", "");

			/*
			 * Ignore first byte [43] of the response and then read each two
			 * bytes.
			 */
			int begin = 0; // start at 2nd byte
			int end = begin + 4; // end at 4th byte

			for (int i = 0; i < howManyTroubleCodes; i++) {
				// read and jump 2 bytes
				if (i % 3 != 0) {
					codes[i] = res.substring(begin, end);
					begin += 4;
					end += 4;
				} else {
					begin += 2;
					end += 2;
					codes[i] = res.substring(begin, end);
					begin += 4;
					end += 4;
				}
			}
		}

		String result = "";
		for (String r : codes) {
			result += r + "\r\n";
		}
		return result;
	}
	
	public String[] getTroubleCodes(){
		String res = getResult();
		ArrayList<String> result=new ArrayList<String>();
		if (!"NODATA".equals(res)) {
			res = res.replaceAll("\r", "");

			/*
			 * Ignore first byte [43] of the response and then read each two
			 * bytes.
			 */
			int begin = 0; // start at 2nd byte
			int end = begin + 4; // end at 4th byte

			for (int i = 0; i < howManyTroubleCodes; i++) {
				// read and jump 2 bytes
				if (i % 3 != 0) {
					codes[i] = res.substring(begin, end);
					begin += 4;
					end += 4;
				} else {
					begin += 2;
					end += 2;
					codes[i] = res.substring(begin, end);
					begin += 4;
					end += 4;
				}
			}
			for(String code :codes){
				if(!code.equals("0000")){
					switch(code.charAt(0)){
					case '0':
						code=code.replaceFirst("0", "P0");
						break;
					case '1':
						code=code.replaceFirst("1", "P1");
						break;
					case '2':
						code=code.replaceFirst("2", "P2");
						break;
					case '3':
						code=code.replaceFirst("3", "P3");
						break;
					case '4':
						code=code.replaceFirst("4", "C0");
						break;
					case '5':
						code=code.replaceFirst("5", "C1");
						break;
					case '6':
						code=code.replaceFirst("6", "C2");
						break;
					case '7':
						code=code.replaceFirst("7", "C3");
						break;
					case '8':
						code=code.replaceFirst("8", "B0");
						break;
					case '9':
						code=code.replaceFirst("9", "B1");
						break;
					case 'a':
						code=code.replaceFirst("a", "B2");
						break;
					case 'A':
						code=code.replaceFirst("A", "B2");
						break;
					case 'b':
						code=code.replaceFirst("b", "B3");
						break;
					case 'B':
						code=code.replaceFirst("B", "B3");
						break;
					case 'c':
						code=code.replaceFirst("c", "U0");
						break;
					case 'C':
						code=code.replaceFirst("C", "U0");
						break;
					case 'd':
						code=code.replaceFirst("d", "U1");
						break;
					case 'D':
						code=code.replaceFirst("D", "U1");
						break;
					case 'e':
						code=code.replaceFirst("e", "U2");
						break;
					case 'E':
						code=code.replaceFirst("E", "U2");
						break;
					case 'f':
						code=code.replaceFirst("f", "U3");
						break;
					case 'F':
						code=code.replaceFirst("F", "U3");
						break;
					}
					result.add(code);
				}
			}
			return result.toArray(new String[0]);
		}
		return null;

	}

	@Override
	public String getFormattedResult() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return AvailableCommandNames.PENDING_TROUBLE_CODES.getValue();
	}
	
	@Override
	public AvailableCommandNames getId() {
		return AvailableCommandNames.PENDING_TROUBLE_CODES;
	}
}
