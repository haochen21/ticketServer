package ticket.server.model.security;

import java.io.Serializable;

public class MerchantLogin implements Serializable {

	private Merchant merchant;

	private LoginResult result;

	private static final long serialVersionUID = 6323639586206221804L;

	public MerchantLogin() {

	}

	public Merchant getMerchant() {
		return merchant;
	}

	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}

	public LoginResult getResult() {
		return result;
	}

	public void setResult(LoginResult result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "Login [merchant=" + merchant + ", result=" + result.getDescription() + "]";
	}
}
