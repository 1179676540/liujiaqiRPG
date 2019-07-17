package rpg.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * 系统资源
 * 
 * @author ljq
 *
 */
public class ThreadResource {

	//	public static ExecutorService monsterThreadPool = Executors.newCachedThreadPool();
	/**
	 * 怪物线程池
	 */
	static ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("怪物线程").build();
	public static ExecutorService monsterThreadPool = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L,
			TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), namedThreadFactory);
}