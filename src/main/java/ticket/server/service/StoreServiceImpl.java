package ticket.server.service;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ticket.server.model.store.Category;
import ticket.server.model.store.Product;
import ticket.server.model.store.ProductStatus;
import ticket.server.repository.security.MerchantRepository;
import ticket.server.repository.store.CategoryRepository;
import ticket.server.repository.store.ProductRepository;

@Service
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class StoreServiceImpl implements StoreService {

	@Autowired
	ProductRepository productRepository;

	@Autowired
	MerchantRepository merchantRepository;

	@Autowired
	CategoryRepository categoryRepository;

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Product saveProduct(Product product) {
		return productRepository.save(product);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Product updateProduct(Product product) {
		Product megerProduct = productRepository.findOne(product.getId());

		megerProduct.setName(product.getName());
		megerProduct.setUnitPrice(product.getUnitPrice());
		megerProduct.setDescription(product.getDescription());
		megerProduct.setUnitsInStock(product.getUnitsInStock());
		megerProduct.setInfinite(product.getInfinite());
		megerProduct.setNeedPay(product.getNeedPay());
		megerProduct.setOpenRange(product.getOpenRange());
		megerProduct.setPayTimeLimit(product.getPayTimeLimit());
		megerProduct.setTakeTimeLimit(product.getTakeTimeLimit());
		megerProduct.setStatus(product.getStatus());
		megerProduct.setCategory(product.getCategory());

		return productRepository.save(megerProduct);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void updateImageSource(Long id, String imageSource) {
		productRepository.updateImageSource(id, imageSource);
	}

	@Override
	public Product findProduct(Long productId) {
		return productRepository.findOne(productId);
	}

	@Override
	public Product findWithMerchant(Long id) {
		return productRepository.findWithMerchant(id);
	}

	@Override
	public List<Product> findProductsByMerchant(Long merchantId, ProductStatus status) {
		return productRepository.findByMerchant(merchantId, status);
	}

	@Override
	public List<Product> findProductsByMerchant(Long merchantId) {
		return productRepository.findByMerchantWithCategory(merchantId);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Category saveCategory(Category category) {
		return categoryRepository.save(category);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Category updateCategory(Category category) {
		Category megerCategory = categoryRepository.findOne(category.getId());
		megerCategory.setName(category.getName());
		megerCategory.setDescription(category.getDescription());
		return categoryRepository.save(megerCategory);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void deleteCategory(Long id) {
		Category category = categoryRepository.findOne(id);
		Collection<Product> products = category.getProducts();
		for (Product p : products) {
			p.setCategory(null);
		}
		categoryRepository.delete(category);
	}

	@Override
	public Category findCategory(Long categoryId) {
		return categoryRepository.findOne(categoryId);
	}

	@Override
	public List<Category> findCategorysByMerchant(Long merchantId) {
		return categoryRepository.findByMerchant(merchantId);
	}

}
