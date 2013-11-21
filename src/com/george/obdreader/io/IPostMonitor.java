/*
 * TODO put header 
 */
package com.george.obdreader.io;


/**
 * TODO put description
 */
public interface IPostMonitor {
	void setListener(IPostListener callback);

	boolean isRunning();

	void executeQueue();
	
	void addJobToQueue(ObdCommandJob job);
	
	void clearQueue();
}