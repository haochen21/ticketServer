package ticket.server.model.security;

public enum OpenRangeType {

	ON("营业时间"), OFF("订单关闭");

	private final String description;

	OpenRangeType(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
	
}
