package ticket.server.repository.security;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ticket.server.model.security.User;
import ticket.server.repository.BaseRepository;

public interface UserRepository extends BaseRepository<User, Long> {

	@Query(value = "select u from User u where u.loginName = :loginName")
	User findByLoginName(@Param("loginName") String loginName);
	
	@Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u where u.loginName = :loginName")
    Boolean existsByLoginName(@Param("loginName") String loginName);
	
	@Query(value = "select u from User u where u.openId = :openId")
	User findByOpenId(@Param("openId") String openId);
	
	@Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u where u.openId = :openId")
    Boolean existsByOpenId(@Param("openId") String openId);
	
	@Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u where u.phone = :phone")
    Boolean existsByPhone(@Param("phone") String phone);
}
