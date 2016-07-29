package com.frame.sdk.async;

import android.os.Handler;
import android.os.Message;

/**
 * 异步线程池执行的任务类
 */
public abstract class ExecuteTask implements Runnable, Comparable<ExecuteTask> {
	/*
	 * 该任务优先级
	 */
	private int priority;
	/*
	 * 该任务flag，唯一标识该任务，具有相同的flag则任务是同一任务,没传入flag则flag为this对象的hash地址
	 */
	private String flag;
	private TaskListener taskListener;
	public static final int LOWEST_PRIORITY = 0;
	public static final int LOWER_PRIORITY = 1;
	public static final int LOW_PRIORITY = 2;
	public static final int MIDDLE_PRIORITY = 3;
	public static final int HIGH_PRIORITY = 4;
	public static final int HIGHER_PRIORITY = 5;
	public static final int HIGHEST_PRIORITY = 6;
	private static Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Object[] obj = (Object[]) msg.obj;
			TaskListener taskListener = (TaskListener) obj[0];
			taskListener.onComplete(obj[1]);
		}
	};

	public ExecuteTask() {
		this.priority = MIDDLE_PRIORITY;
		this.flag = this.toString();
	}

	public ExecuteTask(TaskListener taskListener) {
		this();
		this.taskListener = taskListener;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public void setTaskListener(TaskListener taskListener) {
		this.taskListener = taskListener;
	}

	@Override
	public void run() {
		Object result = onDo();
		if (taskListener != null) {
			Message msg = handler.obtainMessage();
			msg.obj = new Object[] { taskListener, result };
			handler.sendMessage(msg);
		}
	}

	/**
	 * 这个任务要做什么，写在这里，会在线程里执行
	 * 
	 * @return 任务执行结果
	 */
	public abstract Object onDo();

	@Override
	public int compareTo(ExecuteTask another) {
		if (this.priority < another.priority)
			return -1;
		else if (this.priority > another.priority)
			return 1;
		else
			return 0;
	}

	public boolean equals(ExecuteTask another) {
		return this.flag.equals(another.flag);
	}
}
