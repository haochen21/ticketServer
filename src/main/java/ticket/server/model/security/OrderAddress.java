package ticket.server.model.security;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonBackReference;

import ticket.server.model.Constants;

@Entity
@Table(name = "ORDERADDRESS")
public class OrderAddress implements Serializable {

	@Id
	@GeneratedValue(generator = Constants.ID_GENERATOR)
	protected Long id;

	@Column(name = "ADDRESS")
	protected String address;

	@Column(name = "LASTCHECK")
	protected Boolean lastCheck;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CUSTOMER_ID", nullable = false)
	@JsonBackReference
	protected Customer customer;

	private static final long serialVersionUID = -3814716259982657255L;

	public OrderAddress() {

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Boolean getLastCheck() {
		return lastCheck;
	}

	public void setLastCheck(Boolean lastCheck) {
		this.lastCheck = lastCheck;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

}
