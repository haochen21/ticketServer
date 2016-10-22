package ticket.server.repository.security;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ticket.server.model.security.OpenRange;
import ticket.server.repository.BaseRepository;

public interface OpenRangeRepository extends BaseRepository<OpenRange, Long> {

	@Query(value = "select openRange from OpenRange openRange where openRange.merchant.id = :merchantId")
	List<OpenRange> findByMerchant(@Param("merchantId") Long merchantId);
	
}
