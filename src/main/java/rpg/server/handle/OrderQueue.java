package rpg.server.handle;

import java.net.SocketAddress;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;

import rpg.pojo.User;

/**
 * 指令队列
 * @author ljq
 *
 */
@Component
public class OrderQueue {
	private static final int MAX_THREAD_NUM = 4;
	private List<ConcurrentLinkedQueue<Runnable>> queueList = Lists.newArrayList();

	@PostConstruct
	public void initQueue() {
		for (int i = 0; i < MAX_THREAD_NUM; i++) {
			ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<Runnable>();
			queueList.add(queue);
			new OrderConsume(queue).start();;
		}
	}
	
	/**
	 * 指令放入队列未登录
	 * @param runnable
	 * @param address
	 * @param meesage
	 */
	public void addOrder(Runnable runnable, SocketAddress address, String meesage) {
		String[] msg = meesage.split("\\s+");
		String word=msg[1]+msg[2];
		//哈希一致性算法算出bucket找到对应的队列
		int bucket = Hashing.consistentHash(Hashing.sha512().hashString(word, Charsets.UTF_8), queueList.size());
		ConcurrentLinkedQueue<Runnable> concurrentLinkedQueue = queueList.get(bucket);
		//将任务丢入队列
		concurrentLinkedQueue.add(runnable);
	}
	
	/**
	 * 指令放入队列登陆状态下
	 * @param runnable
	 * @param address
	 * @param meesage
	 * @param user
	 */
	public void addOrder(Runnable runnable, SocketAddress address, String meesage,User user) {
		String word=""+user.getAreaid();
		//哈希一致性算法算出bucket找到对应的队列
		int bucket = Hashing.consistentHash(Hashing.sha512().hashString(word, Charsets.UTF_8), queueList.size());
		ConcurrentLinkedQueue<Runnable> concurrentLinkedQueue = queueList.get(bucket);
		//将任务丢入队列
		concurrentLinkedQueue.add(runnable);
	}
}
