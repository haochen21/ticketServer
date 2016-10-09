package ticket.server.model.security;

import java.io.Serializable;

public class CustomerLogin implements Serializable {

	private Customer customer;

	private LoginResult result;

	private static final long serialVersionUID = 6323639586206221804L;

	public CustomerLogin() {

	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public LoginResult getResult() {
		return result;
	}

	public void setResult(LoginResult result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "Login [customer=" + customer + ", result=" + result.getDescription() + "]";
	}
}
