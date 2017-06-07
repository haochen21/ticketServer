package ticket.server.model.security;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import ticket.server.model.Constants;
import ticket.server.model.order.Cart;

@Entity
@Table(name = "CUSTOMER", indexes = { @Index(name = "IDX_CUSTOMER_LOGINNAME", columnList = "LOGINNAME"),
		@Index(name = "IDX_CUSTOMER_OPENID", columnList = "OPENID"),
		@Index(name = "IDX_CUSTOMER_PHONE", columnList = "PHONE"),
		@Index(name = "IDX_USER_CARDNO", columnList = "CARDNO") })
public class Customer implements Serializable {

	@Id
	@GeneratedValue(generator = Constants.ID_GENERATOR)
	protected Long id;

	@Column(name = "LOGINNAME", unique = true)
	@JsonSerialize(using = NameDecodeSerializer.class, as=String.class)
	protected String loginName;

	@Column(name = "OPENID")
	protected String openId;

	@Column(name = "NAME")
	@JsonSerialize(using = NameDecodeSerializer.class, as=String.class)
	protected String name;

	@Column(name = "PSW")
	protected String password;

	@Column(name = "CARDNO", unique = true)
	protected String cardNo;

	@Column(name = "CARDUSED")
	protected Boolean cardUsed = false;

	@Column(name = "PHONE")
	protected String phone;

	@Column(name = "MAIL")
	protected String mail;

	@Column(name = "CITY")
	protected String city;

	@Column(name = "PROVINCE")
	protected String province;

	@Column(name = "COUNTRY")
	protected String country;

	@Column(name = "ADDRESS")
	protected String address;
	
	@Column(name = "HEADIMGURL")
	protected String headImgUrl;

	@Column(name = "ACCOUNT")
	protected BigDecimal account;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(updatable = false)
	@org.hibernate.annotations.CreationTimestamp
	protected Date createdOn;

	@OneToMany(mappedBy = "customer", cascade = { CascadeType.PERSIST }, orphanRemoval = true)
	protected Collection<Cart> carts = new ArrayList<Cart>();

	@ManyToMany(cascade = CascadeType.PERSIST)
	@JoinTable(name = "CUSTOMER_MERCHANT", joinColumns = @JoinColumn(name = "CUSTOMER_ID"), inverseJoinColumns = @JoinColumn(name = "MERCHANT_ID"))
	protected Set<Merchant> merchants = new HashSet<Merchant>();

	@OneToMany(mappedBy = "customer", cascade = { CascadeType.PERSIST }, orphanRemoval = true)
	protected Collection<OrderAddress> orderAddresses = new ArrayList<OrderAddress>();
	
	private static final long serialVersionUID = 89421814089152615L;

	public Customer() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getHeadImgUrl() {
		return headImgUrl;
	}

	public void setHeadImgUrl(String headImgUrl) {
		this.headImgUrl = headImgUrl;
	}

	public BigDecimal getAccount() {
		return account;
	}

	public void setAccount(BigDecimal account) {
		this.account = account;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
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

	public Collection<OrderAddress> getOrderAddresses() {
		return orderAddresses;
	}

	public void setOrderAddresses(Collection<OrderAddress> orderAddresses) {
		this.orderAddresses = orderAddresses;
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
