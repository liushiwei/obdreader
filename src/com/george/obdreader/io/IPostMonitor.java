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
	
	long addJobToQueue(ObdCommandJob job);
	
	boolean removeJobFromQueue(ObdCommandJob job);
	
	void clearQueue();
}