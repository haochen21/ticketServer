package ticket.server.model.security;

public enum LoginResult {

	AUTHORIZED("通过"), LOGINNAMEERROR("用户名不存在"), PASSWORDERROR("密码错误"),	APPROVEDERROR("管理员未审核通过");

	private final String description;

	LoginResult(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
