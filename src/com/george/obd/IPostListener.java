/*
 * TODO put header 
 */
package com.george.obd;

import com.george.obd.io.ObdCommandJob;

/**
 * TODO put description
 */
public interface IPostListener {

	void stateUpdate(ObdCommandJob job);
	
}