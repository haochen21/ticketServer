package ticket.server.repository.security;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ticket.server.model.security.Device;
import ticket.server.repository.BaseRepository;

public interface DeviceRepository extends BaseRepository<Device, Long> {

	@Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM Device d where d.no = :no")
	Boolean existsByNo(@Param("no") String no);

	@Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM Device d where d.phone = :phone")
	Boolean existsByPhone(@Param("phone") String phone);

	@Query(value = "select d from Device d where d.no = :no")
	Device findByNo(@Param("no") String no);
	
	@Query(value = "select d from Device d where d.phone = :phone")
	Device findByPhone(@Param("phone") String phone);
}
