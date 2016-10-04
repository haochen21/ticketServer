package ticket.server.model.order;

public enum CartStatus {

	PURCHASED("订单提交"), DENIED("订单拒绝"), PAYING("订单付款"),CONFIRMED("订单确认"), DELIVERED("订单交付"), CANCELLED("订单取消");

	private final String description;

	CartStatus(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
