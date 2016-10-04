package ticket.server.model.security;

public enum LoginResult {

	AUTHORIZED("成功"), LOGINNAMEERROR("用户不存在"), PASSWORDERROR("密码错误");

	private final String description;

	LoginResult(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
