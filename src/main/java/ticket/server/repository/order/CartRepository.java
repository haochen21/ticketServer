package ticket.server.repository.order;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ticket.server.model.order.Cart;
import ticket.server.model.order.CartStatus;
import ticket.server.repository.BaseRepository;

public interface CartRepository extends BaseRepository<Cart, Long>, CartRepositoryCustom {

	@Query(value = "select c from Cart c where c.merchant.id = :merchantId and c.customer.id = :customerId and c.status in :statuses")
	List<Cart> findByOrder(@Param("merchantId") Long merchantId, @Param("customerId") Long customerId,
			@Param("statuses") List<CartStatus> statuses);

	@Query(value = "select c from Cart c where c.status in :statuses")
	List<Cart> findByStatus(@Param("statuses") List<CartStatus> statuses);

	@Query(value = "select c from Cart c where c.needPay = :needPay and c.status in :statuses")
	List<Cart> findByPayAndStatus(@Param("needPay") Boolean needPay, @Param("statuses") List<CartStatus> statuses);

	@Query(value = "select c from Cart c where c.no = :no")
	Cart findByNo(@Param("no") String no);
	
	@Query(value = "select c from Cart c JOIN FETCH c.merchant JOIN FETCH c.customer LEFT JOIN FETCH c.cartItems item JOIN FETCH item.product where c.id = :id")
	Cart findWithJsonData(@Param("id") Long id);
}