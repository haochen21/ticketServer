package ticket.server.model.security;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.constraints.Size;

import ticket.server.model.Constants;
import ticket.server.model.order.Cart;
import ticket.server.model.store.Category;
import ticket.server.model.store.Product;

@Entity
@DiscriminatorValue("M")
public class Merchant extends User {

	@Column(name = "DEVICENO", unique = true)
	protected String deviceNo;

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
