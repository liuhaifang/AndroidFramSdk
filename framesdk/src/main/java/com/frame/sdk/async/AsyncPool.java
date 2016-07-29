package com.frame.sdk.async;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.frame.sdk.app.FrameConfig;
import com.frame.sdk.util.LogUtils;

/**
 * 异步线程池
 */
public class AsyncPool {
	private BlockingQueue<Runnable> taskQueue;
	private ThreadPoolExecutor threadPool;
	private static AsyncPool asyncPool;

	/**
	 * 一个默认的异步线程池，2个一直保持的线程，最大10个线程，线程活动时间1秒，任务队列为一个具有优先级的无限阻塞队列，
	 * 队列满了且线程达到最大线程个数时会添加任务失败， 采用重复添加策略。
	 */
	private AsyncPool() {
		taskQueue = new PriorityBlockingQueue<Runnable>();
		threadPool = new ThreadPoolExecutor(FrameConfig.CORE_POOL_SIZE, FrameConfig.CACHE_MAX_SIZE, FrameConfig.KEEP_ALIVE_TIME, TimeUnit.MILLISECONDS, taskQueue,
				new ThreadPoolExecutor.DiscardOldestPolicy());
	}

	/**
	 * 单例模式获取一个异步线程池
	 */
	public static AsyncPool getInstance() {
		if (asyncPool == null)
			asyncPool = new AsyncPool();
		return asyncPool;
	}

	/**
	 * 增加紧急任务，保证该任务执行
	 */
	public void addInstantTask(ExecuteTask task) {
		if (isExistTask(task)) {
			LogUtils.w("task is already exist , diacard task ,task.flag==" + task.getFlag());
			return;
		}
		if (threadPool.isShutdown()) {
			new Thread(task).start();
			return;
		}
		int activeCound = threadPool.getActiveCount();
		if (activeCound < threadPool.getMaximumPoolSize()) {
			addTask(task);
			return;
		}
		new Thread(task).start();
	}

	/**
	 * 根据任务的flag删除任务队列中的一个任务
	 * 
	 * @param task
	 *            要删除任务
	 * @return
	 */
	public boolean removeTask(ExecuteTask task) {
		for (int i = 0; i < taskQueue.size(); i++) {
			Iterator<Runnable> iterator = taskQueue.iterator();
			while (iterator.hasNext()) {
				ExecuteTask runTask = (ExecuteTask) iterator.next();
				if (runTask.equals(task)) {
					taskQueue.remove(runTask);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 增加一个任务到任务队列，线程池会自动执行该任务
	 * 
	 * @param task
	 */
	public void addTask(ExecuteTask task) {
		if (threadPool.isShutdown())
			return;
		if (isExistTask(task)) {
			LogUtils.w("task is already exist , diacard task ,task.flag==" + task.getFlag());
			return;
		}
		threadPool.execute(task);
	}

	/**
	 * 关闭线程池
	 */
	public void shutdown() {
		if (!threadPool.isShutdown())
			threadPool.shutdown();
		asyncPool = null;
	}

	/**
	 * task是否存在任务队列中
	 */
	public boolean isExistTask(ExecuteTask task) {
		for (int i = 0; i < taskQueue.size(); i++) {
			Iterator<Runnable> iterator = taskQueue.iterator();
			while (iterator.hasNext()) {
				ExecuteTask runTask = (ExecuteTask) iterator.next();
				if (runTask.equals(task))
					return true;
			}
		}
		return false;
	}

	public int getCorePoolSize() {
		return threadPool.getCorePoolSize();
	}

	public void setCorePoolSize(int corePoolSize) {
		threadPool.setCorePoolSize(corePoolSize);

	}

	public int getMaximumPoolSize() {
		return threadPool.getMaximumPoolSize();
	}

	public void setMaximumPoolSize(int maximumPoolSize) {
		threadPool.setMaximumPoolSize(maximumPoolSize);
	}

	public long getKeepAliveTime() {
		return threadPool.getKeepAliveTime(TimeUnit.MILLISECONDS);
	}

	public void setKeepAliveTime(long keepAliveTime) {
		threadPool.setKeepAliveTime(keepAliveTime, TimeUnit.MILLISECONDS);
	}

}
