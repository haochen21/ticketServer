package ticket.server.test.model;

import org.junit.Test;

import junit.framework.TestCase;
import ticket.server.model.order.CartNo;

public class OrderIdTest {

	@Test
	public void getOrderId() {
		CartNo orderId = new CartNo();
		System.out.println(orderId.toString());
		TestCase.assertNotNull(orderId.toString());
		
		CartNo orderId2 = new CartNo();
		System.out.println(orderId2.toString());
		TestCase.assertNotSame(orderId.toString(),orderId2.toString());
	}

}
