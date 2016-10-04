package ticket.server.model.security;

import java.io.Serializable;

public class Login implements Serializable {

	private User user;
	
	private LoginResult result;
	
	private static final long serialVersionUID = 6323639586206221804L;

	public Login() {

	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public LoginResult getResult() {
		return result;
	}

	public void setResult(LoginResult result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "Login [user=" + user + ", result=" + result.getDescription() + "]";
	}
}
