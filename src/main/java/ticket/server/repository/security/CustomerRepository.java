package ticket.server.repository.security;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ticket.server.model.security.Customer;
import ticket.server.repository.BaseRepository;

public interface CustomerRepository extends BaseRepository<Customer, Long> {

	@Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Customer c where c.loginName = :loginName")
	Boolean existsByLoginName(@Param("loginName") String loginName);

	@Query(value = "select c from Customer c where c.openId = :openId")
	Customer findByOpenId(@Param("openId") String openId);

	@Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Customer c where c.openId = :openId")
	Boolean existsByOpenId(@Param("openId") String openId);

	@Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Customer c where c.phone = :phone")
	Boolean existsByPhone(@Param("phone") String phone);

	@Query(value = "select c from Customer c where c.loginName = :loginName")
	Customer findByLoginName(@Param("loginName") String loginName);

	@Query(value = "select c from Customer c where c.cardNo = :cardNo")
	Customer findByCardNo(@Param("cardNo") String cardNo);

	@Query(value = "select c from Customer c where c.phone like %:phone")
	Customer findByPhone(@Param("phone") String phone);

	@Query(value = "select c from Customer c where c.phone = :phone")
	Customer findByFullPhone(@Param("phone") String phone);

	@Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Customer c where c.cardNo = :cardNo")
	Boolean existsByCardNo(@Param("cardNo") String cardNo);

	@Modifying
	@Query(value = "UPDATE Customer c set c.phone = :phone where c.id = :id")
	void updatePhone(@Param("id") Long id, @Param("phone") String phone);
}
