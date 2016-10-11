package ticket.server.model.order;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

public class CartFilter implements Serializable {

	private Long cartId;
	
	private String no;

	private Long merchantId;

	private Long customerId;

	private Collection<CartStatus> statuses;

	private Boolean needPay;

	private Date createTimeBefore;

	private Date createTimeAfter;

	private Date takeBeginTimeBefore;

	private Date takeBeginTimeAfter;
	
	private Date takeBeginTime;

	private Date takeEndTime;

	private Date takeTime;

	private Long productId;

	private int page;

	private int size;

	private Boolean weixinPaid = false;

	private static final long serialVersionUID = -7728555630999945069L;

	public CartFilter() {

	}

	public Long getCartId() {
		return cartId;
	}

	public void setCartId(Long cartId) {
		this.cartId = cartId;
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public Long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Long merchantId) {
		this.merchantId = merchantId;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public Collection<CartStatus> getStatuses() {
		return statuses;
	}

	public void setStatuses(Collection<CartStatus> statuses) {
		this.statuses = statuses;
	}

	public Boolean getNeedPay() {
		return needPay;
	}

	public void setNeedPay(Boolean needPay) {
		this.needPay = needPay;
	}

	public Date getCreateTimeBefore() {
		return createTimeBefore;
	}

	public void setCreateTimeBefore(Date createTimeBefore) {
		this.createTimeBefore = createTimeBefore;
	}

	public Date getCreateTimeAfter() {
		return createTimeAfter;
	}

	public void setCreateTimeAfter(Date createTimeAfter) {
		this.createTimeAfter = createTimeAfter;
	}

	public Date getTakeBeginTimeBefore() {
		return takeBeginTimeBefore;
	}

	public void setTakeBeginTimeBefore(Date takeBeginTimeBefore) {
		this.takeBeginTimeBefore = takeBeginTimeBefore;
	}

	public Date getTakeBeginTimeAfter() {
		return takeBeginTimeAfter;
	}

	public void setTakeBeginTimeAfter(Date takeBeginTimeAfter) {
		this.takeBeginTimeAfter = takeBeginTimeAfter;
	}

	public Date getTakeBeginTime() {
		return takeBeginTime;
	}

	public void setTakeBeginTime(Date takeBeginTime) {
		this.takeBeginTime = takeBeginTime;
	}

	public Date getTakeEndTime() {
		return takeEndTime;
	}

	public void setTakeEndTime(Date takeEndTime) {
		this.takeEndTime = takeEndTime;
	}

	public Date getTakeTime() {
		return takeTime;
	}

	public void setTakeTime(Date takeTime) {
		this.takeTime = takeTime;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public boolean isWeixinPaid() {
		return weixinPaid;
	}

	public void setWeixinPaid(boolean weixinPaid) {
		this.weixinPaid = weixinPaid;
	}

	public TypedQuery<Long> createCountQuery(EntityManager em) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
		Root<Cart> cart = criteria.from(Cart.class);
		criteria.select(cb.countDistinct(cart.get("id")));
		List<Predicate> predicates = getPredicates(cb, criteria, cart);
		criteria.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
		TypedQuery<Long> query = em.createQuery(criteria);
		if (takeTime != null) {
			query.setParameter("takeTime", takeTime);
		}
		return query;
	}

	public TypedQuery<Cart> createListQuery(EntityManager em) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Cart> criteria = cb.createQuery(Cart.class);
		Root<Cart> cart = criteria.from(Cart.class);
		cart.fetch("merchant", JoinType.INNER);
		cart.fetch("customer", JoinType.INNER);
		cart.fetch("cartItems", JoinType.LEFT);
		criteria.select(cart);
		criteria.distinct(true);

		List<Predicate> predicates = getPredicates(cb, criteria, cart);
		criteria.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));

		criteria.orderBy(cb.desc(cart.get("createdOn")));

		TypedQuery<Cart> query = em.createQuery(criteria);
		if (takeTime != null) {
			query.setParameter("takeTime", takeTime);
		}
		return query;
	}

	private <T> List<Predicate> getPredicates(CriteriaBuilder cb, CriteriaQuery<T> criteria, Root<Cart> root) {
		List<Predicate> predicates = new ArrayList<>();

		if (cartId != null) {
			predicates.add(cb.equal(root.<Long>get("id"), cartId));
		}
		if (no != null && !no.equals("")) {
			predicates.add(cb.like(root.<String>get("no"), "%" + no + "%"));
		}
		if (merchantId != null) {
			predicates.add(cb.equal(root.get("merchant").<Long>get("id"), merchantId));
		}
		if (customerId != null) {
			predicates.add(cb.equal(root.get("customer").<Long>get("id"), customerId));
		}

		if (weixinPaid) {
			predicates.add(cb.isNotNull(root.get("transactionId")));
		}

		if (statuses != null) {
			if (statuses.size() > 1) {
				In<CartStatus> statusPredicate = cb.in(root.<CartStatus>get("status"));
				for (CartStatus status : statuses) {
					statusPredicate.value(status);
				}
				predicates.add(statusPredicate);
			} else {
				predicates.add(cb.equal(root.<CartStatus>get("status"), statuses.iterator().next()));
			}
		}
		if (needPay != null) {
			predicates.add(cb.equal(root.<Boolean>get("needPay"), needPay));
		}

		if (createTimeAfter != null && createTimeBefore != null) {
			predicates.add(cb.between(root.<Date>get("createdOn"), createTimeAfter, createTimeBefore));
		} else if (createTimeAfter != null) {
			predicates.add(cb.greaterThan(root.<Date>get("createdOn"), createTimeAfter));
		} else if (createTimeBefore != null) {
			predicates.add(cb.lessThan(root.<Date>get("createdOn"), createTimeBefore));
		}
		
		if (takeBeginTimeAfter != null && takeBeginTimeBefore != null) {
			predicates.add(cb.between(root.<Date>get("takeBeginTime"), takeBeginTimeAfter, takeBeginTimeBefore));
		} else if (takeBeginTimeAfter != null) {
			predicates.add(cb.greaterThan(root.<Date>get("takeBeginTime"), takeBeginTimeAfter));
		} else if (takeBeginTimeBefore != null) {
			predicates.add(cb.lessThan(root.<Date>get("takeBeginTime"), takeBeginTimeBefore));
		}

		if (takeBeginTime != null && takeEndTime != null) {
			predicates.add(cb.greaterThanOrEqualTo(root.<Date>get("takeBeginTime"), takeBeginTime));
			predicates.add(cb.lessThanOrEqualTo(root.<Date>get("takeEndTime"), takeEndTime));
		}

		if (takeTime != null) {
			predicates.add(cb.between(cb.parameter(Date.class, "takeTime"), root.<Date>get("takeBeginTime"),
					root.<Date>get("takeEndTime")));
		}

		if (productId != null) {
			Subquery<CartItem> sq = criteria.subquery(CartItem.class);
			Root<CartItem> sqRoot = sq.from(CartItem.class);
			List<Predicate> sqPredicates = new ArrayList<>();
			sqPredicates.add(cb.equal(sqRoot.get("product").<Long>get("id"), productId));
			sq.select(sqRoot).where(cb.and(sqPredicates.toArray(new Predicate[sqPredicates.size()])));
			predicates.add(cb.exists(sq));
		}
		return predicates;
	}

	private <T> List<Predicate> getPredicatesByJoin(CriteriaBuilder cb, CriteriaQuery<T> criteria, Root<Cart> root) {
		List<Predicate> predicates = new ArrayList<>();

		if (no != null && !no.equals("")) {
			predicates.add(cb.like(root.<String>get("no"), "%" + no + "%"));
		}
		if (merchantId != null) {
			predicates.add(cb.equal(root.get("merchant").<Long>get("id"), merchantId));
		}
		if (customerId != null) {
			predicates.add(cb.equal(root.get("customer").<Long>get("id"), customerId));
		}

		if (weixinPaid) {
			predicates.add(cb.isNotNull(root.get("transactionId")));
		}

		if (statuses != null) {
			if (statuses.size() > 1) {
				In<CartStatus> statusPredicate = cb.in(root.<CartStatus>get("status"));
				for (CartStatus status : statuses) {
					statusPredicate.value(status);
				}
				predicates.add(statusPredicate);
			} else {
				predicates.add(cb.equal(root.<CartStatus>get("status"), statuses.iterator().next()));
			}
		}
		if (needPay != null) {
			predicates.add(cb.equal(root.<Boolean>get("needPay"), needPay));
		}

		if (createTimeAfter != null && createTimeBefore != null) {
			predicates.add(cb.between(root.<Date>get("createdOn"), createTimeAfter, createTimeBefore));
		} else if (createTimeAfter != null) {
			predicates.add(cb.greaterThan(root.<Date>get("createdOn"), createTimeAfter));
		} else if (createTimeBefore != null) {
			predicates.add(cb.lessThan(root.<Date>get("createdOn"), createTimeBefore));
		}
        
		if (takeBeginTimeAfter != null && takeBeginTimeBefore != null) {
			predicates.add(cb.between(root.<Date>get("takeBeginTime"), takeBeginTimeAfter, takeBeginTimeBefore));
		} else if (takeBeginTimeAfter != null) {
			predicates.add(cb.greaterThan(root.<Date>get("takeBeginTime"), takeBeginTimeAfter));
		} else if (takeBeginTimeBefore != null) {
			predicates.add(cb.lessThan(root.<Date>get("takeBeginTime"), takeBeginTimeBefore));
		}
		
		if (takeBeginTime != null && takeEndTime != null) {
			predicates.add(cb.greaterThanOrEqualTo(root.<Date>get("takeBeginTime"), takeBeginTime));
			predicates.add(cb.lessThanOrEqualTo(root.<Date>get("takeEndTime"), takeEndTime));
		}

		return predicates;
	}

	public TypedQuery<Object[]> createStateByStatus(EntityManager em) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> criteria = cb.createQuery(Object[].class);
		Root<Cart> cart = criteria.from(Cart.class);
		criteria.multiselect(cart.get("status"), cb.countDistinct(cart.get("id")), cb.sum(cart.get("totalPrice")));
		List<Predicate> predicates = getPredicates(cb, criteria, cart);
		criteria.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
		criteria.groupBy(cart.get("status"));
		TypedQuery<Object[]> query = em.createQuery(criteria);
		return query;
	}

	public TypedQuery<Object[]> createStateByProduct(EntityManager em) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> criteria = cb.createQuery(Object[].class);
		Root<Cart> cart = criteria.from(Cart.class);
		Join<Cart, CartItem> item = cart.join("cartItems");
		criteria.multiselect(item.get("product").<Long>get("id"), cart.get("status"), cb.countDistinct(cart.get("id")));
		List<Predicate> predicates = getPredicatesByJoin(cb, criteria, cart);
		criteria.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
		criteria.groupBy(item.get("product").<Long>get("id"), cart.get("status"));
		TypedQuery<Object[]> query = em.createQuery(criteria);
		return query;
	}

	public TypedQuery<Long> createCartNumberByStatus(EntityManager em) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
		Root<Cart> cart = criteria.from(Cart.class);
		criteria.select(cb.countDistinct(cart.get("id")));
		List<Predicate> predicates = getPredicates(cb, criteria, cart);
		criteria.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
		TypedQuery<Long> query = em.createQuery(criteria);
		return query;
	}

	public TypedQuery<BigDecimal> createCartEarningByStatus(EntityManager em) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BigDecimal> criteria = cb.createQuery(BigDecimal.class);
		Root<Cart> cart = criteria.from(Cart.class);
		criteria.select(cb.sum(cart.get("totalPrice")));
		List<Predicate> predicates = getPredicates(cb, criteria, cart);
		criteria.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
		TypedQuery<BigDecimal> query = em.createQuery(criteria);
		return query;
	}

	public TypedQuery<Object[]> createEarningByCreatedon(EntityManager em) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> criteria = cb.createQuery(Object[].class);
		Root<Cart> cart = criteria.from(Cart.class);
		Expression<String> dateFunction = cb.function("DATE_FORMAT", String.class, cart.<Date>get("createdOn"),
				cb.literal("%Y-%m-%d"));
		criteria.multiselect(dateFunction.alias("statDate"), cb.sum(cart.get("totalPrice")));
		List<Predicate> predicates = getPredicates(cb, criteria, cart);
		criteria.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
		criteria.groupBy(dateFunction);
		TypedQuery<Object[]> query = em.createQuery(criteria);
		return query;
	}
}
