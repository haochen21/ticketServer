package ticket.server.model.security;

public enum UserType {

	MERCHANT("商户"), CUSTOMER("客户");

	private final String description;

	UserType(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
