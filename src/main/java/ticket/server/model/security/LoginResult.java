package ticket.server.model.security;

public enum LoginResult {

	AUTHORIZED("�ɹ�"), LOGINNAMEERROR("�û�������"), PASSWORDERROR("�������");

	private final String description;

	LoginResult(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
