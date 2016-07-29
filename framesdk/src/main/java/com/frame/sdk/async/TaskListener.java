package com.frame.sdk.async;

public interface TaskListener {
	/**
	 * 任务执行完成
	 */
	void onComplete(Object result);
}
