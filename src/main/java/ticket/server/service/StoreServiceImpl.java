package ticket.server.service;

import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ticket.server.model.security.OpenRange;
import ticket.server.model.store.Category;
import ticket.server.model.store.Product;
import ticket.server.model.store.ProductStatus;
import ticket.server.repository.security.MerchantRepository;
import ticket.server.repository.security.OpenRangeRepository;
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

	@Autowired
	OpenRangeRepository openRangeRepository;

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Product saveProduct(Product product) {
		// 判断商品是否已经存在
		
		if (product.getOpenRanges() != null) {
			Set<OpenRange> tempOpenRanges = new HashSet<>();
			for (OpenRange openRange : product.getOpenRanges()) {
				tempOpenRanges.add(openRangeRepository.getReference(OpenRange.class, openRange.getId()));

			}
			product.setOpenRanges(tempOpenRanges);
		}
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
		megerProduct.setOpenRanges(product.getOpenRanges());
		megerProduct.setSequence(product.getSequence());
		megerProduct.setCode(product.getCode());

		return productRepository.save(megerProduct);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void updateImageSource(Long id, String imageSource) {
		productRepository.updateImageSource(id, imageSource);
	}

	@Override
	public Product findProduct(Long productId) {
		Product product = productRepository.findOne(productId);
		LocalTime zeroTime = LocalTime.now();
		zeroTime = zeroTime.with(ChronoField.HOUR_OF_DAY, 0);
		zeroTime = zeroTime.with(ChronoField.MINUTE_OF_HOUR, 0);
		zeroTime = zeroTime.with(ChronoField.SECOND_OF_MINUTE, 0);
		zeroTime = zeroTime.with(ChronoField.MILLI_OF_SECOND, 0);
		for (OpenRange op : product.getOpenRanges()) {
			if (op.getBeginTime() == null) {
				op.setBeginTime(java.sql.Time.valueOf(zeroTime));
			}
			if (op.getEndTime() == null) {
				op.setEndTime(java.sql.Time.valueOf(zeroTime));
			}
		}
		return product;
	}

	@Override
	public Product findWithMerchant(Long id) {
		Product product = productRepository.findWithMerchant(id);
		LocalTime zeroTime = LocalTime.now();
		zeroTime = zeroTime.with(ChronoField.HOUR_OF_DAY, 0);
		zeroTime = zeroTime.with(ChronoField.MINUTE_OF_HOUR, 0);
		zeroTime = zeroTime.with(ChronoField.SECOND_OF_MINUTE, 0);
		zeroTime = zeroTime.with(ChronoField.MILLI_OF_SECOND, 0);
		for (OpenRange op : product.getOpenRanges()) {
			if (op.getBeginTime() == null) {
				op.setBeginTime(java.sql.Time.valueOf(zeroTime));
			}
			if (op.getEndTime() == null) {
				op.setEndTime(java.sql.Time.valueOf(zeroTime));
			}
		}
		return product;
	}

	@Override
	public List<Product> findProductsByMerchant(Long merchantId, ProductStatus status) {
		return productRepository.findByMerchant(merchantId, status);
	}

	@Override
	public List<Product> findProductsByMerchant(Long merchantId) {
		List<Product> products = productRepository.findByMerchantWithCategory(merchantId);
		for (Product product : products) {
			LocalTime zeroTime = LocalTime.now();
			zeroTime = zeroTime.with(ChronoField.HOUR_OF_DAY, 0);
			zeroTime = zeroTime.with(ChronoField.MINUTE_OF_HOUR, 0);
			zeroTime = zeroTime.with(ChronoField.SECOND_OF_MINUTE, 0);
			zeroTime = zeroTime.with(ChronoField.MILLI_OF_SECOND, 0);
			for (OpenRange op : product.getOpenRanges()) {
				if (op.getBeginTime() == null) {
					op.setBeginTime(java.sql.Time.valueOf(zeroTime));
				}
				if (op.getEndTime() == null) {
					op.setEndTime(java.sql.Time.valueOf(zeroTime));
				}
			}
		}

		return products;
	}

	@Override
	public List<Product> quickSearch(Long merchantId, String code) {
		List<Product> products = productRepository.quickSearch(merchantId, code);
		for (Product product : products) {
			LocalTime zeroTime = LocalTime.now();
			zeroTime = zeroTime.with(ChronoField.HOUR_OF_DAY, 0);
			zeroTime = zeroTime.with(ChronoField.MINUTE_OF_HOUR, 0);
			zeroTime = zeroTime.with(ChronoField.SECOND_OF_MINUTE, 0);
			zeroTime = zeroTime.with(ChronoField.MILLI_OF_SECOND, 0);
			for (OpenRange op : product.getOpenRanges()) {
				if (op.getBeginTime() == null) {
					op.setBeginTime(java.sql.Time.valueOf(zeroTime));
				}
				if (op.getEndTime() == null) {
					op.setEndTime(java.sql.Time.valueOf(zeroTime));
				}
			}
		}

		return products;
	}

	@Override
	public boolean existProductByName(Long merchantId, String name) {
		return productRepository.existsByName(merchantId, name);
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
		megerCategory.setSequence(category.getSequence());
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
