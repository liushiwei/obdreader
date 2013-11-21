/*
 * TODO put header 
 */
package com.george.obdreader.io;


/**
 * TODO put description
 */
public interface IPostListener {

	void stateUpdate(ObdCommandJob job);
	void deviceConnected(String deviceName);
	
}