package ticket.server.repository.security;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ticket.server.model.security.Merchant;
import ticket.server.repository.BaseRepository;

public interface MerchantRepository extends BaseRepository<Merchant, Long> {

	@Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END FROM Merchant m where m.loginName = :loginName")
    Boolean existsByLoginName(@Param("loginName") String loginName);
	
	@Query(value = "select m from Merchant m where m.openId = :openId")
	Merchant findByOpenId(@Param("openId") String openId);
	
	@Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END FROM Merchant m where m.openId = :openId")
    Boolean existsByOpenId(@Param("openId") String openId);
	
	@Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END FROM Merchant m where m.phone = :phone")
    Boolean existsByPhone(@Param("phone") String phone);
	
	@Query(value = "select m from Merchant m where m.loginName = :loginName")
	Merchant findByLoginName(@Param("loginName") String loginName);
	
	@Query(value = "select m from Merchant m where m.deviceNo = :deviceNo")
	Merchant findByDeviceNo(@Param("deviceNo") String deviceNo);
	
	@Query(value = "select m from Merchant m LEFT JOIN FETCH m.openRanges where m.id = :id")
	Merchant findWithOpenRange(@Param("id") Long id);
	
	@Query(value = "select m from Merchant m where m.name like %:name% or m.shortName like %:name%")
	List<Merchant> findByName(@Param("name") String name);
	
	@Modifying
	@Query(value = "UPDATE Merchant m set m.open = :open where m.id = :id")
	void updateOpen(@Param("id") Long id,@Param("open") Boolean open);
	
	@Modifying
	@Query(value = "UPDATE Merchant m set m.imageSource = :imageSource where m.id = :id")
	void updateImageSource(@Param("id") Long id,@Param("imageSource") String imageSource);
	
	@Modifying
	@Query(value = "UPDATE Merchant m set m.qrCode = :qrCode where m.id = :id")
	void updateQrCode(@Param("id") Long id,@Param("qrCode") String qrCode);
	
	@Modifying
	@Query(value = "UPDATE Merchant m set m.deviceNo = :deviceNo,m.phone = :phone where m.id = :id")
	void register(@Param("id") Long id,@Param("deviceNo") String deviceNo,@Param("phone") String phone);
	
	@Modifying
	@Query(value = "UPDATE Merchant m set m.takeOut = :takeOut where m.id = :id")
	void updateTakeOut(@Param("id") Long id,@Param("takeOut") Boolean takeOut);
	
	@Query(value = "select m from Merchant m LEFT JOIN FETCH m.introduce where m.id = :id")
	Merchant findWithIntroduce(@Param("id") Long id);
}
