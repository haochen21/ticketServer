package ticket.server.model.security;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ticket.server.model.Constants;
import ticket.server.model.order.Cart;
import ticket.server.model.store.Category;
import ticket.server.model.store.Product;

@Entity
@Table(name = "MERCHANT", indexes = { @Index(name = "IDX_MERCHANT_LOGINNAME", columnList = "LOGINNAME"),
		@Index(name = "IDX_MERCHANT_DEVICENO", columnList = "DEVICENO"),
		@Index(name = "IDX_MERCHANT_PHONE", columnList = "PHONE"),
		@Index(name = "IDX_MERCHANT_OPENID", columnList = "OPENID") })
public class Merchant implements Serializable {
    
	@Id
	@GeneratedValue(generator = Constants.ID_GENERATOR)
	protected Long id;

	@NotNull
	@Column(name = "LOGINNAME", unique = true, nullable = false)
	protected String loginName;

	@Column(name = "OPENID")
	protected String openId;

	@NotNull
	@Column(name = "NAME", nullable = false)
	protected String name;

	@NotNull
	@Column(name = "PSW", nullable = false)
	protected String password;
	
	@Column(name = "DEVICENO", unique = true)
	protected String deviceNo;
	
	@Column(name = "PHONE", nullable = false)
	protected String phone;
	
	@Column(name = "MAIL")
	protected String mail;

	@Column(name = "CITY")
	protected String city;
	
	@Column(name = "PROVINCE")
	protected String province;
	
	@Column(name = "COUNTRY")
	protected String country;
	
	@Column(name = "HEADIMGURL")
	protected String headImgUrl;
	
	@Column(name = "ACCOUNT")
	protected BigDecimal account;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(updatable = false)
	@org.hibernate.annotations.CreationTimestamp
	protected Date createdOn;	

	@Column(name = "SHORTNAME")
	protected String shortName;

	@Column(name = "ADDRESS")
	protected String address;

	@Column(name = "DESCRIPTION")
	@Size(min = 0, max = 255)
	protected String description;

	@Column(name = "OPEN")
	protected Boolean open = true;

	@Column(name = "TAKEBYPHONE")
	protected Boolean takeByPhone = true;

	@Column(name = "TAKEBYPHONESUFFIX")
	protected Boolean takeByPhoneSuffix = true;

	@Column(name = "IMAGESOURCE")
	@Size(min = 0, max = 255)
	protected String imageSource;

	@Column(name = "QRCODE")
	@Size(min = 0, max = 255)
	protected String qrCode;

	@OneToMany(mappedBy = "merchant", cascade = { CascadeType.PERSIST, CascadeType.REFRESH }, orphanRemoval = true)
	protected Collection<Category> categorys = new ArrayList<Category>();

	@OneToMany(mappedBy = "merchant", cascade = { CascadeType.PERSIST, CascadeType.REFRESH }, orphanRemoval = true)
	protected Collection<Product> products = new ArrayList<Product>();

	@OneToMany(mappedBy = "merchant", cascade = { CascadeType.PERSIST, CascadeType.REFRESH }, orphanRemoval = true)
	protected Collection<Cart> carts = new ArrayList<Cart>();

	@ElementCollection
	@CollectionTable(name = "OPENRANGE")
	@org.hibernate.annotations.CollectionId(columns = @Column(name = "ID"), type = @org.hibernate.annotations.Type(type = "long"), generator = Constants.ID_GENERATOR)
	protected Collection<OpenRange> openRanges = new ArrayList<OpenRange>();

	private static final long serialVersionUID = -1573726069064463313L;

	public Merchant() {
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

	public String getDeviceNo() {
		return deviceNo;
	}

	public void setDeviceNo(String deviceNo) {
		this.deviceNo = deviceNo;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getOpen() {
		return open;
	}

	public void setOpen(Boolean open) {
		this.open = open;
	}

	public Boolean getTakeByPhone() {
		return takeByPhone;
	}

	public void setTakeByPhone(Boolean takeByPhone) {
		this.takeByPhone = takeByPhone;
	}

	public Boolean getTakeByPhoneSuffix() {
		return takeByPhoneSuffix;
	}

	public void setTakeByPhoneSuffix(Boolean takeByPhoneSuffix) {
		this.takeByPhoneSuffix = takeByPhoneSuffix;
	}

	public String getImageSource() {
		return imageSource;
	}

	public void setImageSource(String imageSource) {
		this.imageSource = imageSource;
	}

	public String getQrCode() {
		return qrCode;
	}

	public void setQrCode(String qrCode) {
		this.qrCode = qrCode;
	}

	public Collection<Category> getCategorys() {
		return categorys;
	}

	public void setCategorys(Collection<Category> categorys) {
		this.categorys = categorys;
	}

	public Collection<Product> getProducts() {
		return products;
	}

	public void setProducts(Collection<Product> products) {
		this.products = products;
	}

	public Collection<Cart> getCarts() {
		return carts;
	}

	public void setCarts(Collection<Cart> carts) {
		this.carts = carts;
	}

	public Collection<OpenRange> getOpenRanges() {
		return openRanges;
	}

	public void setOpenRanges(Collection<OpenRange> openRanges) {
		this.openRanges = openRanges;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((deviceNo == null) ? 0 : deviceNo.hashCode());
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
		Merchant other = (Merchant) obj;
		if (deviceNo == null) {
			if (other.deviceNo != null)
				return false;
		} else if (!deviceNo.equals(other.deviceNo))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Merchant [loginName=" + loginName + ",deviceNo=" + deviceNo + ", shortName=" + shortName + "]";
	}
}
