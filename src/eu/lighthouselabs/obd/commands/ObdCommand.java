/*
 * TODO put header
 */

package eu.lighthouselabs.obd.commands;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import eu.lighthouselabs.obd.enums.AvailableCommandNames;

import android.util.Log;

/**
 * TODO put description
 */
public abstract class ObdCommand {

	protected ArrayList<Integer> buffer = null;
	protected String cmd = null;
	protected boolean useImperialUnits = false;
	protected String rawData = null;
	protected int priority = 0;
	protected int currentPriority = 0;
	private int delay = 0;

	/**
	 * Default ctor to use
	 * 
	 * @param command
	 *            the command to send
	 */
	public ObdCommand(String command) {
		this.cmd = command.replaceAll(" ", "");
		this.cmd += "\r";
		this.buffer = new ArrayList<Integer>();
	}

	/**
	 * Prevent empty instantiation
	 */
	private ObdCommand() {
	}

	/**
	 * Copy ctor.
	 * 
	 * @param other
	 *            the ObdCommand to copy.
	 */
	public ObdCommand(ObdCommand other) {
		this(other.cmd);
	}

	/**
	 * Sends the OBD-II request and deals with the response.
	 * 
	 * This method CAN be overriden in fake commands.
	 */
	public void run(InputStream in, OutputStream out) throws IOException,
			InterruptedException {
		long startTime=System.currentTimeMillis();   //获取开始时间
		sendCommand(out);
		long sendTime=System.currentTimeMillis();   //获取开始时间
		Log.e("ObdCommand", "Send Take time = "+(sendTime-startTime));
		readResult(in);
		long endTime=System.currentTimeMillis(); //获取结束时间
		Log.e("ObdCommand", "Read Take time = "+(endTime-sendTime));
	}

	/**
	 * Sends the OBD-II request.
	 * 
	 * This method may be overriden in subclasses, such as ObMultiCommand or
	 * TroubleCodesObdCommand.
	 * 
	 * @param cmd
	 *            The command to send.
	 */
	protected void sendCommand(OutputStream out) throws IOException,
			InterruptedException {

		// write to OutputStream, or in this case a BluetoothSocket
		out.write(cmd.getBytes());
		out.flush();

		/*
		 * HACK GOLDEN HAMMER ahead!!
		 * 
		 * TODO clean
		 * 
		 * Due to the time that some systems may take to respond, let's give it
		 * 500ms.
		 */
		if(delay>0)
		Thread.sleep(delay);
	}

	/**
	 * Resends this command.
	 * 
	 * 
	 */
	protected void resendCommand(OutputStream out) throws IOException,
			InterruptedException {
		out.write("\r".getBytes());
		out.flush();
		/*
		 * HACK GOLDEN HAMMER ahead!!
		 * 
		 * TODO clean this
		 * 
		 * Due to the time that some systems may take to respond, let's give it
		 * 500ms.
		 */
		// Thread.sleep(250);
	}

	/**
	 * Reads the OBD-II response.
	 * 
	 * This method may be overriden in subclasses, such as ObdMultiCommand.
	 */
	protected void readResult(InputStream in) throws IOException {
		byte b = 0;
		StringBuffer res = new StringBuffer();

		// read until '>' arrives
		while ((char) (b = (byte) in.read()) != '>'&&b!=-1)
			if ((char) b != ' ')
				res.append((char) b);

		/*
		 * Imagine the following response 41 0c 00 0d.
		 * 
		 * ELM sends strings!! So, ELM puts spaces between each "byte". And pay
		 * attention to the fact that I've put the word byte in quotes, because
		 * 41 is actually TWO bytes (two chars) in the socket. So, we must do
		 * some more processing..
		 */
		//
		rawData = res.toString().trim();

		// clear buffer
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

	/**
	 * @return the raw command response in string representation.
	 */
	public String getResult() {
		if(rawData!=null){
		if (rawData.contains("SEARCHING") || rawData.contains("DATA")) {
			rawData = "NODATA";
		}
		}

		return rawData;
	}

	/**
	 * @return a formatted command response in string representation.
	 */
	public abstract String getFormattedResult();

	/******************************************************************
	 * Getters & Setters
	 */

	/**
	 * @return a list of integers
	 */
	public ArrayList<Integer> getBuffer() {
		return buffer;
	}

	/**
	 * Returns this command in string representation.
	 * 
	 * @return the command
	 */
	public String getCommand() {
		return cmd;
	}

	/**
	 * @return true if imperial units are used, or false otherwise
	 */
	public boolean useImperialUnits() {
		return useImperialUnits;
	}

	/**
	 * Set to 'true' if you want to use imperial units, false otherwise. By
	 * default this value is set to 'false'.
	 * 
	 * @param isImperial
	 */
	public void useImperialUnits(boolean isImperial) {
		this.useImperialUnits = isImperial;
	}

	/**
	 * @return the OBD command name.
	 */
	public abstract String getName();
	
	/**
	 * @return the OBD command id.
	 */
	public abstract AvailableCommandNames getId();
	

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getCurrentPriority() {
		return currentPriority;
	}

	public void setCurrentPriority(int currentPriority) {
		this.currentPriority = currentPriority;
	}
	
	public void setDelay(int delay){
		this.delay = delay;
	}
	
	public int getDelay(){
		return delay;
	}
	
	

}