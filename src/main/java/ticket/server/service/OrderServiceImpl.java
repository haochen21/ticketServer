package ticket.server.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ticket.server.exception.BuyEmptyProductException;
import ticket.server.exception.CartPaidException;
import ticket.server.exception.CartStatusException;
import ticket.server.exception.MerchantDiscountException;
import ticket.server.exception.ProductPriceException;
import ticket.server.exception.TakeTimeException;
import ticket.server.model.order.Cart;
import ticket.server.model.order.CartFilter;
import ticket.server.model.order.CartItem;
import ticket.server.model.order.CartProductStat;
import ticket.server.model.order.CartStatus;
import ticket.server.model.order.CartStatusStat;
import ticket.server.model.security.Customer;
import ticket.server.model.security.DiscountType;
import ticket.server.model.security.Merchant;
import ticket.server.model.store.Product;
import ticket.server.model.store.ProductStatus;
import ticket.server.process.NeedPayCarMonitor;
import ticket.server.process.NoNeedPayCartMonitor;
import ticket.server.repository.order.CartItemRepository;
import ticket.server.repository.order.CartRepository;
import ticket.server.repository.security.CustomerRepository;
import ticket.server.repository.security.MerchantRepository;
import ticket.server.repository.store.ProductRepository;

@Service
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class OrderServiceImpl implements OrderService {

	@Autowired
	MerchantRepository merchantRepository;
	
	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	ProductRepository productRepository;

	@Autowired
	CartRepository cartRepository;

	@Autowired
	CartItemRepository cartItemRepository;

	@Autowired
	NeedPayCarMonitor needPayCarMonitor;

	@Autowired
	NoNeedPayCartMonitor noNeedPayCartMonitor;
	
	private final static Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = { BuyEmptyProductException.class,
			ProductPriceException.class, TakeTimeException.class, MerchantDiscountException.class })
	public Cart purchaseCart(Cart cart)
			throws BuyEmptyProductException, ProductPriceException, TakeTimeException, MerchantDiscountException {
		Merchant dbMerchant = merchantRepository.findOne(cart.getMerchant().getId());

		if (cart.getMerchant().getDiscountType() != dbMerchant.getDiscountType()) {
			logger.info("merchant id: " + dbMerchant.getId() + " discountType is change....");
			throw new MerchantDiscountException(dbMerchant, cart.getMerchant().getDiscount());
		}

		if (dbMerchant.getDiscountType() == DiscountType.PERCNET
				&& !cart.getMerchant().getDiscount().equals(dbMerchant.getDiscount())) {
			logger.info("merchant id: " + dbMerchant.getId() + " discount percent is change....");
			throw new MerchantDiscountException(dbMerchant, cart.getMerchant().getDiscount());
		}

		if (dbMerchant.getDiscountType() == DiscountType.AMOUNT
				&& !cart.getMerchant().getAmount().equals(dbMerchant.getAmount())) {
			logger.info("merchant id: " + dbMerchant.getId() + " discount amount is change....");
			throw new MerchantDiscountException(dbMerchant, cart.getMerchant().getDiscount());
		}

		Customer dbCustomer = customerRepository.findOne(cart.getCustomer().getId());
		cart.setCustomer(dbCustomer);
		
		boolean needPay = false;
		int payTimeLimit = Integer.MAX_VALUE;
		int takeTimeLimit = Integer.MIN_VALUE;
		BigDecimal totalPrice = new BigDecimal(0);
		boolean userCurrentOpenTime = true;

		for (CartItem cartItem : cart.getCartItems()) {
			Product product = productRepository.findOne(cartItem.getProduct().getId());
			if (product.getUnitPrice().floatValue() != cartItem.getUnitPrice().floatValue()) {
				logger.info("product id: " + product.getId() + " price is not equal");
				throw new ProductPriceException(product, cartItem.getUnitPrice());
			}
			if (!product.getOpenRange()) {
				userCurrentOpenTime = false;
			}
			cartItem.setProduct(product);
			cartItem.setName(product.getName());
			if (dbMerchant.getDiscountType() != null) {
				if (dbMerchant.getDiscountType() == DiscountType.PERCNET) {
					cartItem.setUnitPrice(product.getUnitPrice().multiply(new BigDecimal(dbMerchant.getDiscount())));
					
				} else if (dbMerchant.getDiscountType() == DiscountType.AMOUNT) {
					cartItem.setUnitPrice(product.getUnitPrice().subtract(new BigDecimal(dbMerchant.getAmount())));
				}
			} else {
				cart.setTotalPrice(totalPrice);
			}
			cartItem.setTotalPrice(cartItem.getUnitPrice().multiply(new BigDecimal(cartItem.getQuantity())));
			
			if (product.getStatus() == ProductStatus.OFFLINE) {
				logger.info("product id: " + product.getId() + " is offline");
				throw new BuyEmptyProductException();
			}
			if (!product.getInfinite() && product.getUnitsInStock() < cartItem.getQuantity()) {
				logger.info("product id: " + product.getId() + " ,in stock is: " + product.getUnitsInStock()
						+ ", order number is: " + cartItem.getQuantity());
				throw new BuyEmptyProductException();
			}
			totalPrice = totalPrice.add(cartItem.getTotalPrice());
			
			if (!product.getInfinite()) {
				product.setUnitsInStock(product.getUnitsInStock() - cartItem.getQuantity());
			}
			product.setUnitsInOrder(product.getUnitsInOrder() + cartItem.getQuantity());

			if (product.getNeedPay()) {
				needPay = true;
				payTimeLimit = product.getPayTimeLimit() < payTimeLimit ? product.getPayTimeLimit() : payTimeLimit;
			}
			takeTimeLimit = product.getTakeTimeLimit() > takeTimeLimit ? product.getTakeTimeLimit() : takeTimeLimit;
		}
		cart.setNeedPay(needPay);
		cart.setTotalPrice(totalPrice);
		
		if (!needPay) {
			payTimeLimit = 0;
			cart.setStatus(CartStatus.CONFIRMED);
		} else {
			cart.setStatus(CartStatus.PURCHASED);
		}

		Instant now = Instant.now();

		cart.setPayTimeLimit(payTimeLimit);
		Instant payTime = now.plus(payTimeLimit, ChronoUnit.MINUTES);
		cart.setPayTime(Date.from(payTime));

		cart.setTakeTimeLimit(takeTimeLimit);
		Instant takeTime = now.plus(takeTimeLimit, ChronoUnit.MINUTES);
		cart.setTakeTime(Date.from(takeTime));

		if (cart.getTakeBeginTime()!= null && cart.getTakeBeginTime().before(Date.from(now)) && cart.getTakeEndTime().after(Date.from(now))) {
			if (!userCurrentOpenTime) {
				logger.info("take time is not in current open time");
				throw new TakeTimeException(cart.getTakeTime(), cart.getTakeBeginTime());
			}
		}

		// save cart
		cartRepository.saveAndFlush(cart);
		// send cart to process queue
		if (needPay) {
			needPayCarMonitor.addCartToQueue(cart);
		} else {
			noNeedPayCartMonitor.addCartToQueue(cart);
		}
		logger.info("create a new cart: " + cart.toString());
		return cart;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = CartStatusException.class)
	public Cart payingCart(Long cartId) throws CartStatusException {
		Cart cart = cartRepository.findOne(cartId);
		if (cart.getStatus() != CartStatus.PURCHASED) {
			throw new CartStatusException(cart);
		}
		cart.setStatus(CartStatus.PAYING);
		Cart dbCart = cartRepository.saveAndFlush(cart);
		needPayCarMonitor.updateCart(dbCart);
		logger.info("paying cart: " + dbCart.toString());
		return cart;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = CartStatusException.class)
	public Cart payingFailCart(Long cartId) throws CartStatusException {
		Cart cart = cartRepository.findOne(cartId);
		if (cart.getStatus() != CartStatus.PAYING) {
			throw new CartStatusException(cart);
		}
		cart.setStatus(CartStatus.PURCHASED);
		cart = cartRepository.saveAndFlush(cart);
		needPayCarMonitor.updateCart(cart);
		logger.info("payingFail cart: " + cart.toString());
		return cart;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = CartStatusException.class)
	public Cart paidCart(Long cartId) throws CartStatusException {
		Cart cart = cartRepository.findOne(cartId);
		if (cart.getStatus() != CartStatus.PAYING) {
			throw new CartStatusException(cart);
		}
		cart.setStatus(CartStatus.CONFIRMED);
		cart = cartRepository.saveAndFlush(cart);
		needPayCarMonitor.removeCartInQueue(cart);
		logger.info("paid cart: " + cart.toString());
		return cart;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = { CartPaidException.class })
	public Cart weixinPaidCart(String no, String transactionId) throws CartStatusException, CartPaidException {
		Cart cart = cartRepository.findByNo(no);
		if (cart.getTransactionId() != null && cart.getTransactionId().equals("")) {
			throw new CartPaidException(cart);
		}
		cart.setStatus(CartStatus.CONFIRMED);
		cart.setTransactionId(transactionId);
		cart = cartRepository.saveAndFlush(cart);
		needPayCarMonitor.removeCartInQueue(cart);
		logger.info("paid cart: " + cart.toString());
		return cart;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = CartStatusException.class)
	public Cart deliverCart(Long cartId) throws CartStatusException {
		Cart cart = cartRepository.findOne(cartId);
		if (cart.getStatus() != CartStatus.CONFIRMED) {
			throw new CartStatusException(cart);
		}
		cart.setStatus(CartStatus.DELIVERED);
		cart.getCartItems().size();
		for (CartItem cartItem : cart.getCartItems()) {
			Product product = productRepository.findOne(cartItem.getProduct().getId());
			product.setUnitsInOrder(product.getUnitsInOrder() - cartItem.getQuantity());
		}
		cart = cartRepository.saveAndFlush(cart);
		if (!cart.getNeedPay()) {
			noNeedPayCartMonitor.removeCartInQueue(cart);
		}
		logger.info("delivered cart: " + cart.toString());
		return cart;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = CartStatusException.class)
	public Cart cancelCart(Long cartId) throws CartStatusException {
		Cart cart = cartRepository.findOne(cartId);

		if (cart.getNeedPay() && (cart.getStatus() != CartStatus.PURCHASED && cart.getStatus() != CartStatus.PAYING)) {
			throw new CartStatusException(cart);
		}
		if (!cart.getNeedPay() && cart.getStatus() != CartStatus.CONFIRMED) {
			throw new CartStatusException(cart);
		}

		cart.setStatus(CartStatus.CANCELLED);
		cart.getCartItems().size();
		for (CartItem cartItem : cart.getCartItems()) {
			Product product = productRepository.findOne(cartItem.getProduct().getId());
			if (!product.getInfinite()) {
				product.setUnitsInStock(product.getUnitsInStock() + cartItem.getQuantity());
			}
			product.setUnitsInOrder(product.getUnitsInOrder() - cartItem.getQuantity());
		}
		logger.info("cancelled cart: " + cart.toString());
		return cart;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void deleteCart(Long cartId) {
		cartRepository.delete(cartId);
	}

	@Override
	public Cart findCartByNo(String no) {
		return cartRepository.findByNo(no);
	}

	@Override
	public Cart findWithJsonData(Long id) {
		return cartRepository.findWithJsonData(id);
	}

	@Override
	public List<Cart> findCartByOrder(Long merchantId, Long customerId, List<CartStatus> statuses) {
		return cartRepository.findByOrder(merchantId, customerId, statuses);
	}

	@Override
	public List<Cart> findCartByStatus(List<CartStatus> statuses) {
		return cartRepository.findByStatus(statuses);
	}

	@Override
	public List<Cart> findCartByPayAndStatus(Boolean needPay, List<CartStatus> statuses) {
		return cartRepository.findByPayAndStatus(needPay, statuses);
	}

	@Override
	public List<Cart> findCartByFilter(CartFilter filter, Pageable pageable) {
		Integer startIndex = null;
		Integer pageSize = null;
		if (pageable != null) {
			startIndex = pageable.getPageNumber();
			pageSize = pageable.getPageSize();
		}
		return cartRepository.findByFilter(filter, startIndex, pageSize);
	}

	@Override
	public Page<Cart> pageCartByFilter(CartFilter filter, Pageable pageable) {
		Integer startIndex = null;
		Integer pageSize = null;
		if (pageable != null) {
			startIndex = pageable.getPageNumber();
			pageSize = pageable.getPageSize();
		}
		List<Cart> carts = cartRepository.findByFilter(filter, startIndex, pageSize);
		Long count = cartRepository.countByFilter(filter);
		Page<Cart> page = null;
		if (pageable == null) {
			if (count == 0) {
				count = 1l;
			}
			page = new PageImpl<>(carts, new PageRequest(0, Integer.parseInt("" + count)), count);
		} else {
			page = new PageImpl<>(carts, pageable, count);
		}
		return page;
	}

	@Override
	public List<CartStatusStat> statCartByStatus(CartFilter filter) {
		return cartRepository.statByStatus(filter);
	}

	@Override
	public List<Product> statCartByProduct(CartFilter filter) {
		List<Product> products = productRepository.findByMerchantWithCategory(filter.getMerchantId());
		List<CartProductStat> dbStats = cartRepository.statByProduct(filter);

		for (Product product : products) {
			product.setTakeNumber(new Long(0));
			product.setUnTakeNumber(new Long(0));

			for (CartProductStat dbStat : dbStats) {
				if (dbStat.getProductId().equals(product.getId())) {
					product.setTakeNumber(dbStat.getTakeNumber());
					product.setUnTakeNumber(dbStat.getUnTakeNumber());
				}
			}
		}
		return products;
	}

	@Override
	public Long statCartNumber(CartFilter filter) {
		return cartRepository.statCartNumber(filter);
	}

	@Override
	public BigDecimal statCartEarning(CartFilter filter) {
		return cartRepository.statCartEarning(filter);
	}

	@Override
	public Map<String, BigDecimal> statEarningByCreatedon(CartFilter filter) {
		Map<String, BigDecimal> stats = new TreeMap<>();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		Instant beginInstant = Instant.ofEpochMilli(filter.getTakeBeginTimeAfter().getTime());
		LocalDate beginLocalDate = LocalDateTime.ofInstant(beginInstant, ZoneId.systemDefault()).toLocalDate();

		Instant endInstant = Instant.ofEpochMilli(filter.getTakeBeginTimeBefore().getTime());
		LocalDate endLocalDate = LocalDateTime.ofInstant(endInstant, ZoneId.systemDefault()).toLocalDate();

		Period period = Period.between(beginLocalDate, endLocalDate);
		for (int i = 0; i <= period.getDays(); i++) {
			LocalDate locaDate = beginLocalDate.plusDays(i);
			stats.put(locaDate.format(formatter), new BigDecimal(0));
		}

		Map<String, BigDecimal> earningStats = cartRepository.statEarningByCreatedon(filter);
		for (Map.Entry<String, BigDecimal> entry : earningStats.entrySet()) {
			String key = entry.getKey();
			BigDecimal value = entry.getValue();
			stats.put(key, value);
		}

		return stats;
	}

}
