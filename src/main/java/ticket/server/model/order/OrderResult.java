package ticket.server.model.order;

import java.io.Serializable;

public class OrderResult implements Serializable {

	private boolean result;

	private String error;

	private Cart cart;

	private static final long serialVersionUID = 2027877347801446306L;

	public OrderResult() {

	}

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public Cart getCart() {
		return cart;
	}

	public void setCart(Cart cart) {
		this.cart = cart;
	}

	@Override
	public String toString() {
		return "OrderResult [result=" + result + ", error=" + error + "]";
	}
}
