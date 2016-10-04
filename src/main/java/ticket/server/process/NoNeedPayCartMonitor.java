package ticket.server.process;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ticket.server.model.order.Cart;
import ticket.server.model.order.CartStatus;
import ticket.server.service.OrderService;

public class NoNeedPayCartMonitor {

	@Autowired
	protected OrderService cartService;

	protected DelayQueue<Cart> cartQueue;

	protected ExecutorService executor;

	protected int processNum;

	private final static Logger logger = LoggerFactory.getLogger(NoNeedPayCartMonitor.class);

	public NoNeedPayCartMonitor(int processNum) {
		this.processNum = processNum;
	}

	@PostConstruct
	public void start() {
		cartQueue = new DelayQueue<>();
		executor = Executors.newFixedThreadPool(processNum);
		processCart();
		addNotClosedCartToQueue();
	}

	protected void processCart() {
		for (int i = 0; i < processNum; i++) {
			executor.submit(new Runnable() {
				@Override
				public void run() {
					while (!Thread.interrupted()) {
						Cart cart = null;
						try {
							cart = cartQueue.take();
							Cart dbCart = cartService.cancelCart(cart.getId());
							logger.info("canel cart," + dbCart);
						} catch (Exception ex) {
							logger.info("cart process error", ex);
							if (cart != null) {
								cart.setDelayTime(0);
								cartQueue.put(cart);
							}
						}
					}
				}
			});
		}
	}

	/**
	 * 系统关闭时，一些客户订单没有被处理 系统启动时，装载客户订单
	 */
	protected void addNotClosedCartToQueue() {
		List<CartStatus> statuses = new ArrayList<>();
		statuses.add(CartStatus.CONFIRMED);
		// 不需要支付定单默认状态为：CONFIRMED
		List<Cart> carts = cartService.findCartByPayAndStatus(false, statuses);

		for (Cart cart : carts) {
			addCartToQueue(cart);
		}
	}

	public void addCartToQueue(Cart cart) {
		Instant now = Instant.now();
		long delay = 0;
		if (now.isAfter(cart.getTakeTime().toInstant())) {
			delay = 0;
		} else {
			delay = Duration.between(now, cart.getTakeTime().toInstant()).toMillis();
		}
		cart.setDelayTime(delay);
		cartQueue.put(cart);
		logger.info("add cart to process queue," + cart.toString() + ",delay millseconds is: " + delay
				+ ",queue size is:" + cartQueue.size());
	}

	public void removeCartInQueue(Cart cart) {
		cartQueue.remove(cart);
		logger.info("remove cart in queue," + cart.toString()+ ",queue size is:" + cartQueue.size());
	}
}
