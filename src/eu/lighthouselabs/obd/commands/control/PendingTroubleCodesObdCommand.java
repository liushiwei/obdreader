/*
 * TODO put header
 */
package eu.lighthouselabs.obd.commands.control;

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

	@Override
	public String getFormattedResult() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return AvailableCommandNames.PENDING_TROUBLE_CODES.getValue();
	}
}
