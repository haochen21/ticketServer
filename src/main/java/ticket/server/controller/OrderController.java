package ticket.server.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.jpa.JpaOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ticket.server.exception.BuyEmptyProductException;
import ticket.server.exception.CartPaidException;
import ticket.server.exception.CartStatusException;
import ticket.server.exception.MerchantDiscountException;
import ticket.server.exception.ProductPriceException;
import ticket.server.exception.TakeTimeException;
import ticket.server.message.SendCartJsonExecutor;
import ticket.server.model.order.Cart;
import ticket.server.model.order.CartFilter;
import ticket.server.model.order.CartNo;
import ticket.server.model.order.CartStatus;
import ticket.server.model.order.CartStatusStat;
import ticket.server.model.order.OrderResult;
import ticket.server.model.security.Customer;
import ticket.server.model.security.Merchant;
import ticket.server.model.security.NickNameEnCode;
import ticket.server.model.store.Product;
import ticket.server.process.NeedPayCarMonitor;
import ticket.server.process.NoNeedPayCartMonitor;
import ticket.server.service.OrderService;
import ticket.server.service.SecurityService;

@RestController
@RequestMapping("order")
public class OrderController {

	@Autowired
	OrderService orderService;

	@Autowired
	SecurityService securityService;

	@Autowired
	NeedPayCarMonitor needPayCarMonitor;

	@Autowired
	NoNeedPayCartMonitor noNeedPayCartMonitor;

	@Autowired
	SendCartJsonExecutor sendCartJsonExecutor;
	
	private final static Logger logger = LoggerFactory.getLogger(OrderController.class);

	@RequestMapping(value = "/cart/page", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public Page<Cart> pageCartByFilter(@RequestBody CartFilter filter) {
		Page<Cart> page = orderService.pageCartByFilter(filter, new PageRequest(filter.getPage(), filter.getSize()));
		return page;
	}

	@RequestMapping(value = "/cart/list", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public List<Cart> listCartByFilter(@RequestBody CartFilter filter) {
		List<Cart> carts = orderService.findCartByFilter(filter, new PageRequest(filter.getPage(), filter.getSize()));
		return carts;
	}

	@RequestMapping(value = "/cart/stat/status", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public List<CartStatusStat> statCartByStatus(@RequestBody CartFilter filter) {
		return orderService.statCartByStatus(filter);
	}

	@RequestMapping(value = "/cart/stat/product", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public List<Product> statCartByProduct(@RequestBody CartFilter filter) {
		return orderService.statCartByProduct(filter);
	}

	@RequestMapping(value = "/cart/stat/number", method = RequestMethod.POST, consumes = "application/json")
	public Long statCartNumber(@RequestBody CartFilter filter) {
		return orderService.statCartNumber(filter);
	}

	@RequestMapping(value = "/cart/stat/earning", method = RequestMethod.POST, consumes = "application/json")
	public BigDecimal statCartEarning(@RequestBody CartFilter filter) {
		return orderService.statCartEarning(filter);
	}

	@RequestMapping(value = "/cart/stat/earning/createdOn", method = RequestMethod.POST, consumes = "application/json")
	public Map<String, BigDecimal> statEarningByCreatedOn(@RequestBody CartFilter filter) {
		return orderService.statEarningByCreatedon(filter);
	}

	@RequestMapping(value = "/cart/purchase", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public OrderResult purchaseCart(@RequestBody Cart cart) {
		logger.info("purchase order: " + cart.getCustomer().getLoginName() + ", " + cart.getMerchant().getLoginName());

		if(cart.getName()!= null && !cart.getName().equals("")){
			String name = NickNameEnCode.INSTANCE.encode(cart.getName());
			cart.setName(name);
		}
		
		CartNo cartNo = new CartNo();
		cart.setNo(cartNo.toHexString());

		OrderResult result = new OrderResult();
		boolean process = true;
		while (process) {
			try {
				Cart dbCart = orderService.purchaseCart(cart);

				sendCartJsonExecutor.addCartToQueue(cart);

				process = false;
				result.setResult(true);
				result.setError("");
				result.setCart(dbCart);
			} catch (JpaOptimisticLockingFailureException ex) {
				logger.info("purchase order fail...", ex);
				process = true;
			} catch (BuyEmptyProductException ex) {
				logger.info("purchase order fail...", ex);
				result.setResult(false);
				result.setError("商品库存不满足购买数量");
				process = false;
			} catch (ProductPriceException ex) {
				logger.info("purchase order fail...", ex);
				result.setResult(false);
				result.setError("商品价格已改变");
				process = false;
			} catch (TakeTimeException ex) {
				logger.info("purchase order fail...", ex);
				result.setResult(false);
				result.setError("订单提交时间晚于店铺开始营业时间");
				process = false;
			} catch (MerchantDiscountException ex) {
				logger.info("purchase order fail...", ex);
				result.setResult(false);
				result.setError("商家商品折扣已经改变");
				process = false;
			} catch (Exception ex) {
				logger.info("purchase order fail...", ex);
				process = false;
			}
		}
		return result;
	}

	@RequestMapping(value = "/cart/paying/{id}", method = RequestMethod.GET, produces = "application/json")
	public OrderResult payingCart(@PathVariable Long id) {
		logger.info("paying order: " + id);

		OrderResult result = new OrderResult();

		boolean process = true;
		while (process) {
			try {
				Cart dbCart = orderService.payingCart(id);

				sendCartJsonExecutor.addCartToQueue(dbCart);

				process = false;
				result.setResult(true);
				result.setError("");
				result.setCart(dbCart);
			} catch (JpaOptimisticLockingFailureException ex) {
				logger.info("paying order fail...", ex);
				process = true;
			} catch (CartStatusException ex) {
				logger.info("paying order fail...", ex);
				result.setResult(false);
				result.setError("订单当前状态不能付款");
				process = false;
			}
		}
		return result;
	}

	@RequestMapping(value = "/cart/paid/{id}", method = RequestMethod.GET, produces = "application/json")
	public OrderResult paidCart(@PathVariable Long id) {
		logger.info("paid order: " + id);

		OrderResult result = new OrderResult();

		boolean process = true;
		while (process) {
			try {
				Cart dbCart = orderService.paidCart(id);

				sendCartJsonExecutor.addCartToQueue(dbCart);

				process = false;
				result.setResult(true);
				result.setError("");
				result.setCart(dbCart);
			} catch (JpaOptimisticLockingFailureException ex) {
				logger.info("paying order fail...", ex);
				process = true;
			} catch (CartStatusException ex) {
				logger.info("paying order fail...", ex);
				result.setResult(false);
				result.setError("订单当前状态不能付款成功");
				process = false;
			}
		}
		return result;
	}

	@RequestMapping(value = "/cart/weixin/paid", method = RequestMethod.POST, produces = "application/json")
	public OrderResult paidCartByWeixin(@RequestParam(value = "no", required = true) String no,
			@RequestParam(value = "transactionId", required = true) String transactionId) {
		logger.info("weixin paid order,no: " + no + ", transactionId: " + transactionId);

		OrderResult result = new OrderResult();
		boolean process = true;
		while (process) {
			try {
				Cart dbCart = orderService.weixinPaidCart(no, transactionId);

				sendCartJsonExecutor.addCartToQueue(dbCart);

				process = false;
				result.setResult(true);
				result.setError("");
				result.setCart(dbCart);
			} catch (JpaOptimisticLockingFailureException ex) {
				logger.info("paying order fail...", ex);
				process = true;
			} catch (CartStatusException ex) {
				logger.info("paying order fail...", ex);
				result.setResult(false);
				result.setError("订单当前状态不能付款成功");
				process = false;
			} catch (CartPaidException ex) {
				logger.info("paying order fail...", ex);
				result.setResult(false);
				result.setError("订单已经支付");
				process = false;
			}
		}
		return result;
	}

	@RequestMapping(value = "/cart/deliver/{id}", method = RequestMethod.GET, produces = "application/json")
	public OrderResult deliverCart(@PathVariable Long id) {
		logger.info("deliver order: " + id);

		OrderResult result = new OrderResult();

		boolean process = true;
		while (process) {
			try {
				Cart dbCart = orderService.deliverCart(id);

				sendCartJsonExecutor.addCartToQueue(dbCart);

				process = false;
				result.setResult(true);
				result.setError("");
				result.setCart(dbCart);
			} catch (JpaOptimisticLockingFailureException ex) {
				logger.info("paying order fail...", ex);
				process = true;
			} catch (CartStatusException ex) {
				logger.info("paying order fail...", ex);
				result.setResult(false);
				result.setError("订单当前状态交付订单");
				process = false;
			}
		}
		return result;
	}

	@RequestMapping(value = "/cart/device/confirmOrder", method = RequestMethod.POST, produces = "application/json")
	public Page<Cart> takeCartByDevice(@RequestParam(value = "deviceNo", required = true) String deviceNo,
			@RequestParam(value = "cardNo", required = false) String cardNo,
			@RequestParam(value = "phone", required = false) String phone,
			@RequestParam(value = "takeTime", required = false) Long takeTime) {
		logger.info("device query confirmed order,deviceNo: " + deviceNo + ", cardNo: " + cardNo + ", phone: " + phone);

		Merchant merchant = securityService.findMerchantByDeviceNo(deviceNo);
		if (merchant == null) {
			logger.info("can't find merchant,deviceNo is: " + deviceNo);
			return null;
		}

		List<Customer> customers = new ArrayList<>();
		if (cardNo != null && !cardNo.equals("")) {
			Customer customer = securityService.findCustomerByCardNo(cardNo);
			if (customer != null) {
				customers.add(customer);
			}
			if (customers.size() == 0) {
				logger.info("can't find customer,cardNo: " + cardNo + ", phone: " + phone);
				// 如果消费者不存在，返回卡号没使用状态
				List<Cart> carts = new ArrayList<Cart>();
				Cart cart = new Cart();
				cart.setCardUsed(false);
				carts.add(cart);
				Page<Cart> page = new PageImpl<>(carts, new PageRequest(0, 1), 0);
				return page;
			}
		} else if (phone != null && !phone.equals("") && phone.length() == 11) {
			if (merchant.getTakeByPhone()) {
				Customer customer = securityService.findCustomerByFullPhone(phone);
				if (customer != null) {
					customers.add(customer);
				}
			}
			if (customers.size() == 0) {
				logger.info("can't find customer,cardNo: " + cardNo + ", phone: " + phone);
				// 如果消费者不存在，返回卡号没使用状态
				List<Cart> carts = new ArrayList<Cart>();
				Cart cart = new Cart();
				cart.setCardUsed(false);
				carts.add(cart);
				Page<Cart> page = new PageImpl<>(carts, new PageRequest(0, 1), 0);
				return page;
			}
		}		

		CartFilter filter = new CartFilter();
		filter.setMerchantId(merchant.getId());
		if(customers.size() > 0) {
			filter.setCustomerIds(customers.stream().map(Customer::getId).collect(Collectors.toList()));
		}else if(phone != null && !phone.equals("") && phone.length() != 11){
			filter.setTakeNo(phone);
		}		

		List<CartStatus> statuses = new ArrayList<>();
		statuses.add(CartStatus.CONFIRMED);
		filter.setStatuses(statuses);

		if (takeTime == null) {
			filter.setTakeTime(new Date());
		} else {
			filter.setTakeTime(new Date(takeTime));
		}

		Page<Cart> page = orderService.pageCartByFilter(filter, null);
		if (page.getContent().size() > 0) {
			// 如果第一次使用卡号获取订单，修改消费者卡使用状态为已使用
			if (cardNo != null && !cardNo.equals("")
					&& (customers.get(0).getCardUsed() == null || !customers.get(0).getCardUsed())) {
				customers.get(0).setCardUsed(true);
				securityService.updateCustomer(customers.get(0));
				for (Cart cart : page.getContent()) {
					cart.getCustomer().setCardUsed(true);
				}
			}
		} else {
			// 如果订单不存在，判断是不是第一次使用卡，如果是第一次使用，返回标记位
			if (cardNo != null && !cardNo.equals("")) {
				Cart cart = new Cart();
				if (customers.get(0).getCardUsed() == null) {
					cart.setCardUsed(false);
				} else {
					cart.setCardUsed(customers.get(0).getCardUsed());
				}
				List<Cart> carts = new ArrayList<Cart>();
				carts.add(cart);
				page = new PageImpl<>(carts, new PageRequest(0, 1), 0);
			}
		}
		return page;
	}

	/**
	 * app测试使用
	 * 
	 * @param cartId
	 * @return
	 */
	@RequestMapping(value = "/cart/device/takeById", method = RequestMethod.POST, produces = "application/json")
	public Page<Cart> takeCartById(@RequestParam(value = "cartId", required = true) Long cartId) {

		CartFilter filter = new CartFilter();
		filter.setCartId(cartId);

		Page<Cart> page = orderService.pageCartByFilter(filter, null);
		if (page.getContent().size() > 0) {
			for (Cart cart : page.getContent()) {
				cart.getCustomer().setCardUsed(true);
			}
		} else {
			Cart cart = new Cart();
			cart.setCardUsed(true);
			List<Cart> carts = new ArrayList<Cart>();
			carts.add(cart);
			page = new PageImpl<>(carts, new PageRequest(0, 1), 0);
		}
		return page;
	}

	@RequestMapping(value = "/cart/print/{id}", method = RequestMethod.GET, produces = "application/json")
	public void takeCartByDevice(@PathVariable Long id) {
		logger.info("manual print cart,id is: {}",id);
		sendCartJsonExecutor.manualPrint(id);
	}
}