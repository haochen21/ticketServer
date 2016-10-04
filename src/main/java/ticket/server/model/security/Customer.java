package ticket.server.model.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import ticket.server.model.order.Cart;

@Entity
@DiscriminatorValue("C")
public class Customer extends User {
	
	@Column(name = "CARDNO", unique = true)
	protected String cardNo;

	@Column(name = "CARDUSED")
	protected Boolean cardUsed;
	
	@OneToMany(mappedBy = "customer", cascade = { CascadeType.PERSIST }, orphanRemoval = true)
	protected Collection<Cart> carts = new ArrayList<Cart>();

	@ManyToMany(cascade = CascadeType.PERSIST)
	@JoinTable(name = "CUSTOMER_MERCHANT", joinColumns = @JoinColumn(name = "CUSTOMER_ID"), inverseJoinColumns = @JoinColumn(name = "MERCHANT_ID"))
	protected Set<Merchant> merchants = new HashSet<Merchant>();
	
	private static final long serialVersionUID = 89421814089152615L;

	public Customer() {
		super();
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public Boolean getCardUsed() {
		return cardUsed;
	}

	public void setCardUsed(Boolean cardUsed) {
		this.cardUsed = cardUsed;
	}

	public Collection<Cart> getCarts() {
		return carts;
	}

	public void setCarts(Collection<Cart> carts) {
		this.carts = carts;
	}

	public Set<Merchant> getMerchants() {
		return merchants;
	}

	public void setMerchants(Set<Merchant> merchants) {
		this.merchants = merchants;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((cardNo == null) ? 0 : cardNo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Customer other = (Customer) obj;
		if (cardNo == null) {
			if (other.cardNo != null)
				return false;
		} else if (!cardNo.equals(other.cardNo))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Customer [loginName=" + loginName + ", cardNo=" + cardNo + "]";
	}

}
