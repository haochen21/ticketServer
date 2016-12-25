package ticket.server.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ticket.server.exception.BuyEmptyProductException;
import ticket.server.exception.CartPaidException;
import ticket.server.exception.CartStatusException;
import ticket.server.exception.MerchantDiscountException;
import ticket.server.exception.ProductPriceException;
import ticket.server.exception.TakeTimeException;
import ticket.server.model.order.Cart;
import ticket.server.model.order.CartFilter;
import ticket.server.model.order.CartStatus;
import ticket.server.model.order.CartStatusStat;
import ticket.server.model.store.Product;

public interface OrderService {

	Cart purchaseCart(Cart cart) throws BuyEmptyProductException, ProductPriceException, TakeTimeException,MerchantDiscountException;

	Cart payingCart(Long cartId) throws CartStatusException;

	Cart payingFailCart(Long cartId) throws CartStatusException;

	Cart paidCart(Long cartId) throws CartStatusException;
	
	Cart weixinPaidCart(String no,String transactionId) throws CartStatusException,CartPaidException;

	Cart deliverCart(Long cartId) throws CartStatusException;

	Cart cancelCart(Long cartId) throws CartStatusException;
	
	void deleteCart(Long cartId);

	Cart findCartByNo(String no);

	Cart findWithJsonData(Long id);

	List<Cart> findCartByOrder(Long merchantId, Long customerId, List<CartStatus> statuses);

	List<Cart> findCartByStatus(List<CartStatus> statuses);

	List<Cart> findCartByPayAndStatus(Boolean needPay, List<CartStatus> statuses);

	List<Cart> findCartByFilter(CartFilter filter, Pageable pageable);

	Page<Cart> pageCartByFilter(CartFilter filter, Pageable pageable);

	List<CartStatusStat> statCartByStatus(CartFilter filter);
	
	List<Product> statCartByProduct(CartFilter filter);
	
	Long statCartNumber(CartFilter filter);
	
	BigDecimal statCartEarning(CartFilter filter);
	
	Map<String,BigDecimal> statEarningByCreatedon(CartFilter filter);
}
