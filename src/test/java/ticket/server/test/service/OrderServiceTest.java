package ticket.server.test.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.jpa.JpaOptimisticLockingFailureException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import junit.framework.TestCase;
import ticket.server.config.HibernateJpaConfig;
import ticket.server.config.JmsConfig;
import ticket.server.config.ServiceConfig;
import ticket.server.exception.BuyEmptyProductException;
import ticket.server.exception.CartStatusException;
import ticket.server.exception.ProductPriceException;
import ticket.server.exception.TakeTimeException;
import ticket.server.message.SendCartJsonExecutor;
import ticket.server.model.order.Cart;
import ticket.server.model.order.CartFilter;
import ticket.server.model.order.CartItem;
import ticket.server.model.order.CartNo;
import ticket.server.model.order.CartStatus;
import ticket.server.model.order.CartStatusStat;
import ticket.server.model.security.Customer;
import ticket.server.model.security.Merchant;
import ticket.server.model.store.Product;
import ticket.server.service.OrderService;
import ticket.server.service.SecurityService;
import ticket.server.service.StoreService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { HibernateJpaConfig.class, JmsConfig.class, ServiceConfig.class })
public class OrderServiceTest {

	@Autowired
	StoreService productService;

	@Autowired
	OrderService orderService;

	@Autowired
	SecurityService securityService;
	
	@Autowired
	SendCartJsonExecutor sendCartJsonExecutor;

	private Long cartId;

	private boolean hasPaying = false;

	private boolean hasPayingFail = false;

	private boolean hasPaid = false;

	private boolean hasDelivered = false;

	// @Test
	//@PostConstruct
	public void createCart() {
		boolean process = true;

		while (process) {
			Cart cart = new Cart();

			CartNo cartNo = new CartNo();
			cart.setNo(cartNo.toHexString());

			Merchant merchant = securityService.findMerchant(new Long(11));
			cart.setMerchant(merchant);

			Customer customer = securityService.findCustomer(new Long(6));
			cart.setCustomer(customer);

			cart.setStatus(CartStatus.PURCHASED);

			Calendar beginCalendar = Calendar.getInstance();
			beginCalendar.set(Calendar.HOUR_OF_DAY, 23);
			beginCalendar.set(Calendar.MINUTE, 0);
			beginCalendar.set(Calendar.SECOND, 0);
			beginCalendar.set(Calendar.MILLISECOND, 0);

			Calendar endCalendar = Calendar.getInstance();
			endCalendar.set(Calendar.HOUR_OF_DAY, 23);
			endCalendar.set(Calendar.MINUTE, 59);
			endCalendar.set(Calendar.SECOND, 59);
			endCalendar.set(Calendar.MILLISECOND, 0);

			cart.setTakeBeginTime(beginCalendar.getTime());
			cart.setTakeEndTime(endCalendar.getTime());

			CartItem item1 = new CartItem();
			Product p1 = productService.findProduct(new Long(81));
			item1.setProduct(p1);
			item1.setName(p1.getName());
			item1.setQuantity(2);
			item1.setUnitPrice(p1.getUnitPrice());
			item1.setTotalPrice(p1.getUnitPrice().multiply(new BigDecimal(item1.getQuantity())));
			item1.setQuantity(2);
			item1.setCart(cart);
			cart.getCartItems().add(item1);

			CartItem item2 = new CartItem();
			Product p2 = productService.findProduct(new Long(86));
			item2.setProduct(p2);
			item2.setName(p2.getName());
			item2.setQuantity(2);
			item2.setUnitPrice(p2.getUnitPrice());
			item2.setTotalPrice(p2.getUnitPrice().multiply(new BigDecimal(item2.getQuantity())));
			item2.setCart(cart);
			cart.getCartItems().add(item2);

			// CartItem item3 = new CartItem();
			// Product p3 = productService.findProduct(new Long(431));
			// item3.setProduct(p3);
			// item3.setName(p3.getName());
			// item3.setPrice(p3.getUnitPrice());
			// item3.setQuantity(2);
			// item3.setCart(cart);
			// cart.getCartItems().add(item3);

			try {
				cart = orderService.purchaseCart(cart);
				sendCartJsonExecutor.addCartToQueue(cart);
				cartId = cart.getId();
				process = false;

				TestCase.assertNotNull(cart.getId());
				TestCase.assertTrue(cart.getCartItems().size() > 0);
			} catch (BuyEmptyProductException ex) {
				ex.printStackTrace();
				process = false;
			} catch (ProductPriceException ex) {
				ex.printStackTrace();
				process = false;
			} catch (TakeTimeException ex) {
				ex.printStackTrace();
				process = false;
			} catch (JpaOptimisticLockingFailureException ex) {
				ex.printStackTrace();
				process = true;
			}
		}

	}

	//@Scheduled(initialDelay = 30 * 1000, fixedDelay = 5000)
	public void payingOrder() {
		boolean process = true;
		while (!hasPaying && process) {
			try {
				Cart cart = orderService.payingCart(cartId);
				sendCartJsonExecutor.addCartToQueue(cart);
				process = false;
				hasPaying = true;
			} catch (JpaOptimisticLockingFailureException ex) {
				ex.printStackTrace();
				process = true;
			} catch (CartStatusException ex) {
				ex.printStackTrace();
				process = false;
				hasPaying = true;
			}
		}
	}

	// @Scheduled(initialDelay = 45 * 1000, fixedDelay = 5000)
	public void payingFailOrder() {
		boolean process = true;
		while (!hasPayingFail && process) {
			try {
				Cart cart = orderService.payingFailCart(cartId);
				sendCartJsonExecutor.addCartToQueue(cart);
				process = false;
				hasPayingFail = true;
				hasPaying = false;
			} catch (JpaOptimisticLockingFailureException ex) {
				ex.printStackTrace();
				process = true;
			} catch (CartStatusException ex) {
				ex.printStackTrace();
				process = false;
				hasPayingFail = true;
			}
		}
	}

	//@Scheduled(initialDelay = 60 * 1000, fixedDelay = 5000)
	public void paidOrder() {
		boolean process = true;
		while (!hasPaid && process) {
			try {
				Cart cart = orderService.paidCart(cartId);
				sendCartJsonExecutor.addCartToQueue(cart);
				process = false;
				hasPaid = true;
			} catch (JpaOptimisticLockingFailureException ex) {
				ex.printStackTrace();
				process = true;
			} catch (CartStatusException ex) {
				ex.printStackTrace();
				process = false;
				hasPaid = true;
			}
		}
	}

	//@Scheduled(initialDelay = 80 * 1000, fixedDelay = 5000)
	public void deliverOrder() {
		boolean process = true;
		while (!hasDelivered && process) {
			try {
				Cart cart = orderService.deliverCart(cartId);
				sendCartJsonExecutor.addCartToQueue(cart);
				process = false;
				hasDelivered = true;
			} catch (JpaOptimisticLockingFailureException ex) {
				ex.printStackTrace();
				process = true;
			} catch (CartStatusException ex) {
				ex.printStackTrace();
				process = false;
				hasDelivered = true;
			}
		}
	}

	@Test
	public void findCartByFilter() {
		CartFilter filter = new CartFilter();
		filter.setMerchantId(new Long(11));
		List<Long> customerIds = new ArrayList<>();
		customerIds.add(new Long(146));
		filter.setCustomerIds(customerIds);


		List<CartStatus> statuses = new ArrayList<>();
		//statuses.add(CartStatus.DELIVERED);
		statuses.add(CartStatus.CONFIRMED);
		filter.setStatuses(statuses);
        
		Calendar takeCalendar = Calendar.getInstance();
		takeCalendar.set(Calendar.DAY_OF_MONTH, 16);
		takeCalendar.set(Calendar.HOUR_OF_DAY, 11);
		takeCalendar.set(Calendar.MINUTE, 30);
		takeCalendar.set(Calendar.SECOND, 0);
		takeCalendar.set(Calendar.MILLISECOND, 0);
		filter.setTakeTime(takeCalendar.getTime());
		
		// filter.setNeedPay(true);
        //List<Cart> carts = orderService.findCartByFilter(filter, null);
        //TestCase.assertEquals(carts.size(), 1);
        
		Page<Cart> page = orderService.pageCartByFilter(filter, new PageRequest(0, 1));
		TestCase.assertEquals(page.getTotalElements(), 2);
		TestCase.assertEquals(page.getNumberOfElements(), 1);
		TestCase.assertEquals(page.getContent().size(), 1);
	}

	// @Test
	public void statByStatus() {
		CartFilter filter = new CartFilter();
		filter.setMerchantId(new Long(416));

		List<CartStatus> statuses = new ArrayList<>();
		statuses.add(CartStatus.DELIVERED);
		statuses.add(CartStatus.CONFIRMED);
		filter.setStatuses(statuses);

		Calendar beginCalendar = Calendar.getInstance();
		beginCalendar.set(Calendar.HOUR_OF_DAY, 16);
		beginCalendar.set(Calendar.MINUTE, 30);
		beginCalendar.set(Calendar.SECOND, 0);
		beginCalendar.set(Calendar.MILLISECOND, 0);
		filter.setTakeBeginTime(beginCalendar.getTime());

		Calendar endCalendar = Calendar.getInstance();
		endCalendar.set(Calendar.HOUR_OF_DAY, 19);
		endCalendar.set(Calendar.MINUTE, 0);
		endCalendar.set(Calendar.SECOND, 0);
		endCalendar.set(Calendar.MILLISECOND, 999);
		filter.setTakeEndTime(endCalendar.getTime());

		List<CartStatusStat> stats = orderService.statCartByStatus(filter);
		for (CartStatusStat stat : stats) {
			System.out.println(stat.toString());
		}
	}

	//@Test
	public void statEarningByCreateOn() {
		CartFilter filter = new CartFilter();
		filter.setMerchantId(new Long(416));

		List<CartStatus> statuses = new ArrayList<>();
		statuses.add(CartStatus.DELIVERED);
		statuses.add(CartStatus.CONFIRMED);
		filter.setStatuses(statuses);

		Calendar beginCalendar = Calendar.getInstance();
		beginCalendar.set(Calendar.DAY_OF_MONTH, 1);
		beginCalendar.set(Calendar.HOUR_OF_DAY, 0);
		beginCalendar.set(Calendar.MINUTE, 0);
		beginCalendar.set(Calendar.SECOND, 0);
		beginCalendar.set(Calendar.MILLISECOND, 0);
		filter.setCreateTimeAfter(beginCalendar.getTime());

		Calendar endCalendar = Calendar.getInstance();	
		endCalendar.set(Calendar.DAY_OF_MONTH, 31);
		endCalendar.set(Calendar.HOUR_OF_DAY, 23);
		endCalendar.set(Calendar.MINUTE, 59);
		endCalendar.set(Calendar.SECOND, 59);
		endCalendar.set(Calendar.MILLISECOND, 0);	
		
		filter.setCreateTimeBefore(endCalendar.getTime());

		filter.setNeedPay(true);
		
		Map<String, BigDecimal> values = orderService.statEarningByCreatedon(filter);
		for (Map.Entry<String, BigDecimal> entry : values.entrySet()) {
			String key = entry.getKey();
			BigDecimal value = entry.getValue();
			System.out.println(key + "," + value);
		}
	}

	// @Test
	public void statByProduct() {
		CartFilter filter = new CartFilter();
		filter.setMerchantId(new Long(416));

		List<CartStatus> statuses = new ArrayList<>();
		statuses.add(CartStatus.DELIVERED);
		statuses.add(CartStatus.CONFIRMED);
		filter.setStatuses(statuses);

		filter.setNeedPay(true);

		Calendar beginCalendar = Calendar.getInstance();
		beginCalendar.set(Calendar.HOUR_OF_DAY, 16);
		beginCalendar.set(Calendar.MINUTE, 30);
		beginCalendar.set(Calendar.SECOND, 0);
		beginCalendar.set(Calendar.MILLISECOND, 0);
		filter.setTakeBeginTime(beginCalendar.getTime());

		Calendar endCalendar = Calendar.getInstance();
		endCalendar.set(Calendar.HOUR_OF_DAY, 19);
		endCalendar.set(Calendar.MINUTE, 0);
		endCalendar.set(Calendar.SECOND, 0);
		endCalendar.set(Calendar.MILLISECOND, 999);
		filter.setTakeEndTime(endCalendar.getTime());

		List<Product> products = orderService.statCartByProduct(filter);
		for (Product product : products) {
			System.out.println(product.toString());
		}
	}

	public static void main(String[] args) {
		@SuppressWarnings("resource")
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.register(OrderServiceConfig.class);
		ctx.refresh();
	}
}
