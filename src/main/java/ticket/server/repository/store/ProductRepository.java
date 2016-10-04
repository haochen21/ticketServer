package ticket.server.repository.store;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Modifying;

import ticket.server.model.store.Product;
import ticket.server.model.store.ProductStatus;
import ticket.server.repository.BaseRepository;

public interface ProductRepository extends BaseRepository<Product, Long> {

	@Query(value = "select p from Product p where p.merchant.id = :merchantId and p.status = :status")
	List<Product> findByMerchant(@Param("merchantId") Long merchantId, @Param("status") ProductStatus status);
	
	@Query(value = "select p from Product p LEFT JOIN FETCH p.category where p.merchant.id = :merchantId ")
	List<Product> findByMerchantWithCategory(@Param("merchantId") Long merchantId);
	
	@Query(value = "select p from Product p where p.merchant.id = :merchantId ")
	List<Product> findByMerchant(@Param("merchantId") Long merchantId);
	
	@Query(value = "select p from Product p LEFT JOIN FETCH p.category where p.id = :id ")
	Product findWithMerchant(@Param("id") Long id);
	
	@Modifying
	@Query(value = "UPDATE Product p set p.imageSource = :imageSource where p.id = :id")
	void updateImageSource(@Param("id") Long id,@Param("imageSource") String imageSource);
}
