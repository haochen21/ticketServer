package ticket.server.model.order;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonBackReference;

import ticket.server.model.Constants;
import ticket.server.model.store.Product;

@Entity
@Table(name = "CARTITEM", uniqueConstraints = {
		@UniqueConstraint(name = "UNQ_CARDITEM_PRODUCT_CART", columnNames = { "PRODUCT_ID", "CART_ID" }) }, indexes = {
				@Index(name = "IDX_CARDITEM_CART", columnList = "CART_ID") })
public class CartItem implements Serializable {

	@Id
	@GeneratedValue(generator = Constants.ID_GENERATOR)
	protected Long id;

	@NotNull
	@Column(name = "NAME", nullable = false)
	protected String name;

	@NotNull
	@Column(name = "QUANTITY", nullable = false)
	protected Integer quantity;

	@NotNull
	@Column(name = "UNITPRICE", nullable = false)
	protected BigDecimal unitPrice;

	@NotNull
	@Column(name = "TOTALPRICE", nullable = false)
	protected BigDecimal totalPrice;

	@NotNull
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "PRODUCT_ID", nullable = false)
	protected Product product;

	@NotNull
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "CART_ID", nullable = false)
	@JsonBackReference
	protected Cart cart;

	private static final long serialVersionUID = 6852793237053469465L;

	public CartItem() {

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Cart getCart() {
		return cart;
	}

	public void setCart(Cart cart) {
		this.cart = cart;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cart == null) ? 0 : cart.getNo().hashCode());
		result = prime * result + ((product == null) ? 0 : product.getId().hashCode());
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
		CartItem other = (CartItem) obj;
		if (cart == null) {
			if (other.cart != null)
				return false;
		} else if (!cart.getNo().equals(other.cart.getNo()))
			return false;
		if (product == null) {
			if (other.product != null)
				return false;
		} else if (!product.getId().equals(other.product.getId()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "LineItem [name=" + name + ", quantity=" + quantity + ", totalPrice=" + totalPrice + ", product=" + product
				+ ", cart=" + cart + "]";
	}

}
