package ticket.server.message;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ticket.server.model.order.Cart;

@Component
public class SendCartJsonExecutor {

	private SendCartKafka sendCartKafka;
	
	private BlockingQueue<Cart> cartQueue;

	private ExecutorService executor;

	private int processNum = 2;

	private final static Logger logger = LoggerFactory.getLogger(SendCartJsonExecutor.class);

	public SendCartJsonExecutor() {

	}

	public SendCartKafka getSendCartKafka() {
		return sendCartKafka;
	}

	public void setSendCartKafka(SendCartKafka sendCartKafka) {
		this.sendCartKafka = sendCartKafka;
	}

	@PostConstruct
	public void start() {
		cartQueue = new LinkedBlockingDeque<>();
		executor = Executors.newFixedThreadPool(processNum);
		processCart();
		logger.info("start cart json");
	}

	protected void processCart() {
		for (int i = 0; i < processNum; i++) {
			executor.submit(new Runnable() {
				@Override
				public void run() {
					while (!Thread.interrupted()) {
						try {
							Cart cart = cartQueue.take();
							sendCartKafka.send(cart);
						} catch (Exception ex) {
							logger.info("cart jms error", ex);
						}
					}
				}
			});
		}
	}

	public void addCartToQueue(Cart cart) {
		cartQueue.add(cart);
		logger.info("add cart to jms queue,size is: " + cartQueue.size() + ", cart is :" + cart.toString());
	}

}
