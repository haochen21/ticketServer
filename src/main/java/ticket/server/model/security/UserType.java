package ticket.server.model.security;

public enum UserType {

	MERCHANT("�̻�"), CUSTOMER("�ͻ�");

	private final String description;

	UserType(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
