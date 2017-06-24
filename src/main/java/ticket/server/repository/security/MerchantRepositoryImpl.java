package ticket.server.repository.security;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import ticket.server.model.security.Merchant;
import ticket.server.model.security.NickNameEnCode;

public class MerchantRepositoryImpl implements MerchantRepositoryCustom {

	@PersistenceContext
	private EntityManager em;

	@Override
	public Merchant login(String login) {
		String encodeLoginName = NickNameEnCode.INSTANCE.encode(login);
		Long merchantId;;
		try{
			merchantId = Long.parseLong(login);
		}catch(NumberFormatException ex){
			merchantId = null;
		}
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Merchant> criteria = cb.createQuery(Merchant.class);
		Root<Merchant> merchant = criteria.from(Merchant.class);
		criteria.select(merchant);
		criteria.distinct(true);
		
		List<Predicate> predicates = new ArrayList<>();

		if (merchantId != null) {
			predicates.add(cb.equal(merchant.<Long>get("id"), merchantId));
		}
		predicates.add(cb.equal(merchant.<String>get("loginName"), encodeLoginName));
		predicates.add(cb.equal(merchant.<String>get("name"), login));
		
		criteria.where(cb.or(predicates.toArray(new Predicate[predicates.size()])));

		TypedQuery<Merchant> query = em.createQuery(criteria);
		Merchant dbMerchant = query.getSingleResult();
		return dbMerchant;
	}

}
