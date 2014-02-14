/*
 * TODO put header 
 */
package com.george.obdreader.io;


/**
 * TODO put description
 */
public interface IPostListener {
	/**
	 * 消息回调
	 * @param job 返回的Obd命令对象
	 */
	void stateUpdate(ObdCommandJob job);
	
	/**
	 * 开始连接设备
	 * @param deviceName
	 */
	void connectingDevice(String deviceName);
	/**
	 * 连接成功
	 * @param deviceName 设备名
	 */
	void deviceConnected(String deviceName);
	/**
	 * 连接失败
	 * @param deviceName 设备名
	 */
	void connectFailed(String deviceName);
	
}