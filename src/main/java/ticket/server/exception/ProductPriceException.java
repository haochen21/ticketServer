package ticket.server.exception;

import java.math.BigDecimal;

import ticket.server.model.store.Product;

public class ProductPriceException extends Exception {

	private static final long serialVersionUID = -8505295394797901176L;

	public ProductPriceException(Product p, BigDecimal unitPrice) {
		super("There is not equal price," + p.getId() + ": " + p.getUnitPrice() + ", cartItem price is:" + unitPrice);
	}
}
