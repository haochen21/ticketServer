package ticket.server.repository.security;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ticket.server.model.security.OrderAddress;
import ticket.server.repository.BaseRepository;

public interface OrderAddressRepository extends BaseRepository<OrderAddress, Long> {
	
	@Query(value = "select oa from OrderAddress oa where oa.customer.id = :id and oa.address = :address")
	OrderAddress findByAddress(@Param("id") Long id,@Param("address") String address);
	
	@Query(value = "select oa from OrderAddress oa where oa.customer.id = :id")
	List<OrderAddress> findByCustomer(@Param("id") Long id);
}
