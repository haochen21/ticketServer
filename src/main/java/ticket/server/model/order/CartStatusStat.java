package ticket.server.model.order;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonFormat;

public class CartStatusStat implements Serializable{

	@JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
	protected CartStatus status;
	
	protected BigDecimal price;
	
	protected Long total;
	
	private static final long serialVersionUID = 6787875623668652505L;

	public CartStatusStat(){
		
	}

	public CartStatus getStatus() {
		return status;
	}

	public void setStatus(CartStatus status) {
		this.status = status;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	@Override
	public String toString() {
		return "CartStatusStat [status=" + status + ", price=" + price + ", total=" + total + "]";
	}
}
