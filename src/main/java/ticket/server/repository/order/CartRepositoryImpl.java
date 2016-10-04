package ticket.server.repository.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import ticket.server.model.order.Cart;
import ticket.server.model.order.CartFilter;
import ticket.server.model.order.CartProductStat;
import ticket.server.model.order.CartStatus;
import ticket.server.model.order.CartStatusStat;

public class CartRepositoryImpl implements CartRepositoryCustom {

	@PersistenceContext
	private EntityManager em;

	@Override
	public List<Cart> findByFilter(CartFilter filter, Integer startIndex, Integer pageSize) {
		List<Cart> carts = new ArrayList<>();

		TypedQuery<Cart> query = filter.createListQuery(em);
		if (startIndex != null && pageSize != null) {
			query.setFirstResult(startIndex * pageSize);
			query.setMaxResults(pageSize);
		}
		List<Cart> dbCarts = query.getResultList();
		if (dbCarts != null && dbCarts.size() > 0) {
			carts.addAll(dbCarts);
		}
		return carts;
	}

	@Override
	public Long countByFilter(CartFilter filter) {
		TypedQuery<Long> query = filter.createCountQuery(em);
		Long count = query.getSingleResult();
		return count;
	}

	@Override
	public List<CartStatusStat> statByStatus(CartFilter filter) {
		List<CartStatusStat> stats = new ArrayList<>();

		CartStatusStat confirmed = new CartStatusStat();
		confirmed.setStatus(CartStatus.CONFIRMED);
		confirmed.setTotal(new Long(0));
		confirmed.setPrice(new BigDecimal(0));
		stats.add(confirmed);

		CartStatusStat delivered = new CartStatusStat();
		delivered.setStatus(CartStatus.DELIVERED);
		delivered.setTotal(new Long(0));
		delivered.setPrice(new BigDecimal(0));
		stats.add(delivered);

		TypedQuery<Object[]> query = filter.createStateByStatus(em);
		List<Object[]> result = query.getResultList();
		for (Object[] row : result) {
			CartStatus status = (CartStatus) row[0];
			long total = ((Number) row[1]).longValue();
			BigDecimal price = BigDecimal.valueOf(((Number) row[2]).doubleValue());
			for (CartStatusStat stat : stats) {
				if (stat.getStatus() == status) {
					stat.setPrice(price);
					stat.setTotal(total);
				}
			}
		}

		return stats;
	}

	@Override
	public List<CartProductStat> statByProduct(CartFilter filter) {
		List<CartProductStat> stats = new ArrayList<>();
		TypedQuery<Object[]> query = filter.createStateByProduct(em);
		List<Object[]> result = query.getResultList();
		for (Object[] row : result) {
			long productId = ((Number) row[0]).longValue();
			CartStatus status = (CartStatus) row[1];
			long total = ((Number) row[2]).longValue();

			CartProductStat stat = null;

			for (CartProductStat cartProductStat : stats) {
				if (cartProductStat.getProductId().equals(productId)) {
					stat = cartProductStat;
					break;
				}
			}

			if (stat == null) {
				stat = new CartProductStat();
				stat.setProductId(productId);
				stat.setUnTakeNumber(new Long(0));
				stat.setTakeNumber(new Long(0));
				stats.add(stat);
			}

			if (status == CartStatus.CONFIRMED) {
				stat.setUnTakeNumber(stat.getUnTakeNumber() + total);
			} else if (status == CartStatus.DELIVERED) {
				stat.setTakeNumber(stat.getTakeNumber() + total);
			}
		}
		return stats;
	}

	@Override
	public Long statCartNumber(CartFilter filter) {
		TypedQuery<Long> query = filter.createCartNumberByStatus(em);
		Long count = query.getSingleResult();
		return count;
	}

	@Override
	public BigDecimal statCartEarning(CartFilter filter) {
		TypedQuery<BigDecimal> query = filter.createCartEarningByStatus(em);
		BigDecimal earning = query.getSingleResult();
		return earning;
	}

	@Override
	public Map<String, BigDecimal> statEarningByCreatedon(CartFilter filter) {
		Map<String, BigDecimal> stats = new HashMap<>();
		TypedQuery<Object[]> query = filter.createEarningByCreatedon(em);
		List<Object[]> result = query.getResultList();
		for (Object[] row : result) {
			String dateValue = row[0].toString();
			BigDecimal price = BigDecimal.valueOf(((Number) row[1]).doubleValue());
			stats.put(dateValue, price);
		}
		return stats;
	}

}
