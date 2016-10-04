package ticket.server.exception;

import ticket.server.model.order.Cart;

public class CartPaidException extends Exception {

	private static final long serialVersionUID = 6869535150865912043L;

	public CartPaidException(Cart cart) {
		super("cart has paid error: " + cart.toString());
	}
}
