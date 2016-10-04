package ticket.server.repository.store;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ticket.server.model.store.Category;
import ticket.server.repository.BaseRepository;

public interface CategoryRepository extends BaseRepository<Category, Long> {

	@Query(value = "select c from Category c where c.merchant.id = :merchantId")
	List<Category> findByMerchant(@Param("merchantId") Long merchantId);
}
