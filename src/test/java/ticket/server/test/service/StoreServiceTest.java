package ticket.server.test.service;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import junit.framework.TestCase;
import ticket.server.config.HibernateJpaConfig;
import ticket.server.config.ServiceConfig;
import ticket.server.model.security.Merchant;
import ticket.server.model.store.Product;
import ticket.server.model.store.ProductStatus;
import ticket.server.service.StoreService;
import ticket.server.service.SecurityService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { HibernateJpaConfig.class, ServiceConfig.class })
public class StoreServiceTest {

	@Autowired
	StoreService storeService;

	@Autowired
	SecurityService securityService;

	//@Test
	public void createProduct() {
		Merchant m = securityService.findMerchant(new Long(1));
		Product p = new Product();
		p.setName("’®º¶≈≈");
		p.setInfinite(false);
		p.setUnitsInStock(new Long(100));
		p.setUnitsInOrder(new Long(0));
		p.setUnitPrice(new BigDecimal(15.00));
		p.setStatus(ProductStatus.ONLINE);
		p.setNeedPay(true);
		p.setPayTimeLimit(4);
		p.setTakeTimeLimit(10);
		p.setMerchant(m);
		storeService.saveProduct(p);

		TestCase.assertNotNull(p.getId());
	}

	// @Test
	public void updateProduct() {
		Product p = storeService.findProduct(new Long(11));
		p.setInfinite(false);

		storeService.updateProduct(p);

		TestCase.assertTrue(p.getInfinite());
	}
	
	@Test
	public void updateImageSource(){		
		storeService.updateImageSource(new Long(161), "112ki-ingp");
	}

	//@Test
	public void findProducts() {
		List<Product> products = storeService.findProductsByMerchant(new Long(1), ProductStatus.OFFLINE);
		TestCase.assertEquals(products.size(), 1);
	}

}
