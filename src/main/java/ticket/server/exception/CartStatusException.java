package ticket.server.exception;

import ticket.server.model.order.Cart;

public class CartStatusException extends Exception {

	private static final long serialVersionUID = 6869535150865912043L;

	public CartStatusException(Cart cart) {
		super("cart status error: " + cart.toString());
	}
}
