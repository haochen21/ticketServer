package ticket.server.model.store;

public enum ProductStatus {

	ONLINE("上架"), OFFLINE("下架"), DELETE("删除");

	private final String description;

	ProductStatus(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
