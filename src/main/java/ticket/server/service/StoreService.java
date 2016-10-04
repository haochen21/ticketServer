package ticket.server.service;

import java.util.List;

import ticket.server.model.store.Category;
import ticket.server.model.store.Product;
import ticket.server.model.store.ProductStatus;

public interface StoreService {

	Product saveProduct(Product product);

	Product updateProduct(Product product);
	
	Product findProduct(Long productId);
	
	Product findWithMerchant(Long id);
	
	List<Product> findProductsByMerchant(Long merchantId);
	
	void updateImageSource(Long id,String imageSource);
	
	List<Product> findProductsByMerchant(Long merchantId,ProductStatus status);
	
	Category saveCategory(Category category);

	Category updateCategory(Category category);
	
	void deleteCategory(Long id);
	
	Category findCategory(Long categoryId);
	
	List<Category> findCategorysByMerchant(Long merchantId);
}
