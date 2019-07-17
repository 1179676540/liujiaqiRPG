package rpg.server.handle;

import java.util.concurrent.ConcurrentLinkedQueue;

import lombok.extern.slf4j.Slf4j;

/**
 * 指令消费线程
 * 
 * @author ljq
 *
 */
@Slf4j
public class OrderConsume extends Thread {

	private ConcurrentLinkedQueue<Runnable> orderQueue;

	public OrderConsume(ConcurrentLinkedQueue<Runnable> orderQueue) {
		super();
		this.orderQueue = orderQueue;
	}

	@Override
	public void run() {
		log.info("指令线程启动.........");
		try {
			while (true) {
				while (!orderQueue.isEmpty()) {
					Runnable runnable = orderQueue.poll();
					if (runnable != null) {
						log.info("指令开始执行.........");
						runnable.run();
					}
				}
				Thread.sleep(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
