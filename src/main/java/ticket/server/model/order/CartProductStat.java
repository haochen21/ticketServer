package ticket.server.model.order;

import java.io.Serializable;

public class CartProductStat implements Serializable {

	protected Long productId;

	protected String name;

	protected Long takeNumber;

	protected Long unTakeNumber;

	private static final long serialVersionUID = 6787875623668652505L;

	public CartProductStat() {

	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getTakeNumber() {
		return takeNumber;
	}

	public void setTakeNumber(Long takeNumber) {
		this.takeNumber = takeNumber;
	}

	public Long getUnTakeNumber() {
		return unTakeNumber;
	}

	public void setUnTakeNumber(Long unTakeNumber) {
		this.unTakeNumber = unTakeNumber;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((productId == null) ? 0 : productId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CartProductStat other = (CartProductStat) obj;
		if (productId == null) {
			if (other.productId != null)
				return false;
		} else if (!productId.equals(other.productId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CartProductStat [productId=" + productId + ", name=" + name + ", takeNumber=" + takeNumber
				+ ", unTakeNumber=" + unTakeNumber + "]";
	}

}
