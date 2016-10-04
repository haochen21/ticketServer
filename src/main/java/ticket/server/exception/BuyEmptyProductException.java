package ticket.server.exception;

public class BuyEmptyProductException extends Exception {

	private static final long serialVersionUID = -6969910386241388629L;

	public BuyEmptyProductException() {
		super("There is no enough products to buy");
	}

}
