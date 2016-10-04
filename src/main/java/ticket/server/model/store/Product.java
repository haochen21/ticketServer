package ticket.server.model.store;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import ticket.server.model.Constants;
import ticket.server.model.security.Merchant;

@Entity
@Table(name = "PRODUCT", uniqueConstraints = {
		@UniqueConstraint(name = "UNQ_PRODUCT_MERCHANT_NAME", columnNames = { "MERCHANT_ID", "NAME" }) }, indexes = {
				@Index(name = "IDX_PRODUCT_MERCHANT", columnList = "MERCHANT_ID") })
public class Product implements Serializable {

	@Id
	@GeneratedValue(generator = Constants.ID_GENERATOR)
	protected Long id;

	@NotNull
	@Column(name = "NAME", nullable = false)
	protected String name;

	@NotNull
	@Column(name = "UNITPRICE", nullable = false)
	protected BigDecimal unitPrice;

	@Column(name = "DESCRIPTION")
	@Size(min = 0, max = 255)
	protected String description;

	@Column(name = "UNITSINSTOCK")
	protected Long unitsInStock;

	@Column(name = "UNITSINORDER")
	protected Long unitsInOrder;

	@NotNull
	@Column(name = "INFINITE", nullable = false)
	protected Boolean infinite;

	@NotNull
	@Column(name = "NEEDPAY", nullable = false)
	protected Boolean needPay;

	@NotNull
	@Column(name = "PAYTIMELIMT", nullable = false)
	protected Integer payTimeLimit;

	/**
	 * 如果产品不需要支付，超过时间后订单取消
	 */
	@NotNull
	@Column(name = "TAKETIMELIMT", nullable = false)
	protected Integer takeTimeLimit;

	@Column(name = "IMAGESOURCE")
	@Size(min = 0, max = 255)
	protected String imageSource;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(updatable = false)
	@org.hibernate.annotations.CreationTimestamp
	protected Date createdOn;

	@Temporal(TemporalType.TIMESTAMP)
	@org.hibernate.annotations.CreationTimestamp
	protected Date updatedOn;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS", nullable = false)
	@JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
	protected ProductStatus status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CATEGORY_ID")
	protected Category category;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MERCHANT_ID", nullable = false)
	protected Merchant merchant;

	@Version
	protected long version;

	@Transient
	@JsonSerialize
	protected Long takeNumber;

	@Transient
	@JsonSerialize
	protected Long unTakeNumber;
	
	private static final long serialVersionUID = 3277060162706927687L;

	public Product() {

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

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getUnitsInStock() {
		return unitsInStock;
	}

	public void setUnitsInStock(Long unitsInStock) {
		this.unitsInStock = unitsInStock;
	}

	public Long getUnitsInOrder() {
		return unitsInOrder;
	}

	public void setUnitsInOrder(Long unitsInOrder) {
		this.unitsInOrder = unitsInOrder;
	}

	public Boolean getInfinite() {
		return infinite;
	}

	public void setInfinite(Boolean infinite) {
		this.infinite = infinite;
	}

	public Boolean getNeedPay() {
		return needPay;
	}

	public void setNeedPay(Boolean needPay) {
		this.needPay = needPay;
	}

	public Integer getPayTimeLimit() {
		return payTimeLimit;
	}

	public void setPayTimeLimit(Integer payTimeLimit) {
		this.payTimeLimit = payTimeLimit;
	}

	public Integer getTakeTimeLimit() {
		return takeTimeLimit;
	}

	public void setTakeTimeLimit(Integer takeTimeLimit) {
		this.takeTimeLimit = takeTimeLimit;
	}

	public String getImageSource() {
		return imageSource;
	}

	public void setImageSource(String imageSource) {
		this.imageSource = imageSource;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Merchant getMerchant() {
		return merchant;
	}

	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}

	public ProductStatus getStatus() {
		return status;
	}

	public void setStatus(ProductStatus status) {
		this.status = status;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public Date getUpdatedOn() {
		return updatedOn;
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
		result = prime * result + ((merchant == null) ? 0 : merchant.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Product other = (Product) obj;
		if (merchant == null) {
			if (other.merchant != null)
				return false;
		} else if (!merchant.equals(other.merchant))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Product [name=" + name + ", infinite=" + infinite + ", needPay=" + needPay + ", unitsInStock="
				+ unitsInStock + ", unitsInOrder=" + unitsInOrder + ", status=" + status + ", merchant=" + merchant
				+ "]";
	}
}
