package com.george.obdreader;

import eu.lighthouselabs.obd.commands.ObdCommand;
import eu.lighthouselabs.obd.enums.AvailableCommandNames;

public class OBDCommand extends ObdCommand{
	
	private int pid;
	private String command;
	private int resultSize;
	private String desc;
	private float min;
	private float max;
	private String unit;
	private int formula;
	
	private int A,B,C,D,E;
	private String result;
	
	
	
	public OBDCommand(int pid, String command, int resultSize, String desc,
			float min, float max, String unit, int formula) {
		super(command);
		this.pid = pid;
		this.command = command;
		this.resultSize = resultSize;
		this.desc = desc;
		this.min = min;
		this.max = max;
		this.unit = unit;
		this.formula = formula;
	}
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public int getResultSize() {
		return resultSize;
	}
	public void setResultSize(int resultSize) {
		this.resultSize = resultSize;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public float getMin() {
		return min;
	}
	public void setMin(float min) {
		this.min = min;
	}
	public float getMax() {
		return max;
	}
	public void setMax(float max) {
		this.max = max;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public int getFormula() {
		return formula;
	}
	public void setFormula(int formula) {
		this.formula = formula;
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
	
	
	
	

}
