/*
 * TODO put header 
 */
package com.george.obdreader.io;


/**
 * TODO put description
 */
public interface IPostMonitor {
	void setListener(IPostListener callback);

	/**
	 * 是否连接成功
	 * 
	 * @return
	 */
	boolean isRunning();

	void executeQueue();
	
	/**
	 * 向命令队列中添加新的命令
	 * @param job 新的命令
	 * @return 新的命令ID
	 */
	long addJobToQueue(ObdCommandJob job);
	
	/**
	 * 删除某个命令
	 * @param job
	 * @return 是否删除成功 
	 */
	boolean removeJobFromQueue(ObdCommandJob job);
	
	/**
	 * 清空命令队列
	 */
	void clearQueue();
	
	void connectDevice();
	
}