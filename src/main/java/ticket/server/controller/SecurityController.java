package ticket.server.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import net.coobird.thumbnailator.Thumbnails;
import ticket.server.model.security.Customer;
import ticket.server.model.security.CustomerLogin;
import ticket.server.model.security.Device;
import ticket.server.model.security.Merchant;
import ticket.server.model.security.MerchantLogin;
import ticket.server.model.security.OpenRange;
import ticket.server.service.SecurityService;

@RestController
@RequestMapping("security")
@PropertySource({ "classpath:/META-INF/ticket.properties" })
public class SecurityController {

	@Autowired
	SecurityService securityService;

	@Autowired
	private Environment env;

	private final static Logger logger = LoggerFactory.getLogger(SecurityController.class);

	@RequestMapping(value = "/customer/login", method = RequestMethod.POST, produces = "application/json")
	public CustomerLogin login(@RequestParam(value = "loginName", required = true) String loginName,
			@RequestParam(value = "password", required = true) String password) {
		return securityService.customerLogin(loginName, password);
	}

	@RequestMapping(value = "/merchant/login", method = RequestMethod.POST, produces = "application/json")
	public MerchantLogin merchantLogin(@RequestParam(value = "loginName", required = true) String loginName,
			@RequestParam(value = "password", required = true) String password) {
		return securityService.merchantLogin(loginName, password);
	}

	@RequestMapping(value = "/customer/loginNameExists/{loginName}", method = RequestMethod.GET)
	public @ResponseBody Boolean existsCustomerByLoginName(@PathVariable String loginName) {
		return securityService.existsCustomerByLoginName(loginName);
	}

	@RequestMapping(value = "/merchant/loginNameExists/{loginName}", method = RequestMethod.GET)
	public @ResponseBody Boolean existsMerchantByLoginName(@PathVariable String loginName) {
		return securityService.existsMerchantByLoginName(loginName);
	}

	@RequestMapping(value = "/customer/openIdExists/{openId}", method = RequestMethod.GET)
	public @ResponseBody Boolean existsCustomerByOpenId(@PathVariable String openId) {
		return securityService.existsCustomerByOpenId(openId);
	}

	@RequestMapping(value = "/merchant/openIdExists/{openId}", method = RequestMethod.GET)
	public @ResponseBody Boolean existsMerchantByOpenId(@PathVariable String openId) {
		return securityService.existsMerchantByOpenId(openId);
	}

	@RequestMapping(value = "/customer/openId/{openId}", method = RequestMethod.GET, produces = "application/json")
	public Customer findCustomerByOpenId(@PathVariable String openId) {
		Customer customer = securityService.findCustomerByOpenId(openId);
		return customer;
	}

	@RequestMapping(value = "/merchant/openId/{openId}", method = RequestMethod.GET, produces = "application/json")
	public Merchant findMerchantByOpenId(@PathVariable String openId) {
		Merchant merchant = securityService.findMerchantByOpenId(openId);
		return merchant;
	}

	@RequestMapping(value = "/customer/{id}", method = RequestMethod.GET, produces = "application/json")
	public Customer findCustomerById(@PathVariable Long id) {
		Customer customer = securityService.findCustomer(id);
		return customer;
	}

	@RequestMapping(value = "/merchant/{id}", method = RequestMethod.GET, produces = "application/json")
	public Merchant findMerchantById(@PathVariable Long id) {
		Merchant merchant = securityService.findMerchant(id);
		return merchant;
	}

	@RequestMapping(value = "/customer/phone/{phone}", method = RequestMethod.GET)
	public @ResponseBody Boolean existsCustomerByPhone(@PathVariable String phone) {
		return securityService.existsCustomerByPhone(phone);
	}

	@RequestMapping(value = "/merchant/phone/{phone}", method = RequestMethod.GET)
	public @ResponseBody Boolean existsMerchantByPhone(@PathVariable String phone) {
		return securityService.existsMerchantByPhone(phone);
	}
	
	@RequestMapping(value = "/merchant", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public Merchant createMerchant(@RequestBody Merchant merchant) {
		logger.info("register merchant: " + merchant.toString());
		Merchant dbMerchant = securityService.saveMerchant(merchant);
		return dbMerchant;
	}

	@RequestMapping(value = "/merchant", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	public Merchant modifyMerchant(@RequestBody Merchant merchant) {
		logger.info("modify merchant: " + merchant.toString());
		Merchant dbMerchant = securityService.updateMerchant(merchant);
		return dbMerchant;
	}

	@RequestMapping(value = "/merchant/weixin", method = RequestMethod.PUT, produces = "application/json")
	public Merchant registerMerchantInWeixin(@RequestParam(value = "id", required = true) Long id,
			@RequestParam(value = "phone", required = true) String phone) {
		Merchant merchant = securityService.findMerchant(id);
		merchant.setPhone(phone);
		Device device = securityService.findByPhone(merchant.getPhone());
		if (device != null) {
			merchant.setDeviceNo(device.getNo());
		}
		Merchant dbMerchant = securityService.updateMerchant(merchant);
		return dbMerchant;
	}

	@RequestMapping(value = "/customer", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public Customer createCustomer(@RequestBody Customer customer) {
		logger.info("register customer: " + customer.toString());
		Customer dbCustomer = securityService.saveCustomer(customer);
		return dbCustomer;
	}

	@RequestMapping(value = "/customer", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	public Customer modifyCustomer(@RequestBody Customer customer) {
		logger.info("modify customer: " + customer.toString());
		Customer dbCustomer = securityService.updateCustomer(customer);
		return dbCustomer;
	}

	@RequestMapping(value = "/customer/modifyPhone", method = RequestMethod.PUT, produces = "application/json")
	public @ResponseBody Boolean modifyCustomerPhone(@RequestParam(value = "id", required = true) Long id,
			@RequestParam(value = "phone", required = true) String phone) {
		securityService.updateCustomerPhone(id, phone);
		return true;
	}

	@RequestMapping(value = "/device", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public Device createDevice(@RequestBody Device device) {
		logger.info("create device: " + device.toString());
		Device dbDevice = securityService.createDevice(device);
		return dbDevice;
	}

	@RequestMapping(value = "/device/{no}", method = RequestMethod.GET)
	public @ResponseBody Boolean existsDevice(@PathVariable String no) {
		return securityService.existsDeviceByNo(no);
	}

	@RequestMapping(value = "/device/phone/{phone}", method = RequestMethod.GET)
	public @ResponseBody Boolean existsDeviceByPhone(@PathVariable String phone) {
		return securityService.existsDeviceByPhone(phone);
	}

	@RequestMapping(value = "/card/{cardNo}", method = RequestMethod.GET)
	public @ResponseBody Boolean existsCard(@PathVariable String cardNo) {
		return securityService.existsByCardNo(cardNo);
	}

	@RequestMapping(value = "/customer/password", method = RequestMethod.PUT, produces = "application/json")
	public @ResponseBody Boolean modifyCustomerPassword(@RequestParam(value = "id", required = true) Long id,
			@RequestParam(value = "password", required = true) String password) {
		securityService.modifyCustomerPassword(id, password);
		return true;
	}
	
	@RequestMapping(value = "/merchant/password", method = RequestMethod.PUT, produces = "application/json")
	public @ResponseBody Boolean modifyMerchantPassword(@RequestParam(value = "id", required = true) Long id,
			@RequestParam(value = "password", required = true) String password) {
		securityService.modifyMerchantPassword(id, password);
		return true;
	}

	@RequestMapping(value = "/merchant/open", method = RequestMethod.POST)
	public @ResponseBody Boolean modifyMerchantOpen(@RequestParam(value = "id", required = true) Long id,
			@RequestParam(value = "open", required = true) Boolean open) {
		securityService.updateMerchantOpen(id, open);
		return true;
	}

	@RequestMapping(value = "/merchant/register", method = RequestMethod.POST)
	public @ResponseBody Boolean registerMerchant(@RequestParam(value = "id", required = true) Long id,
			@RequestParam(value = "phone", required = true) String phone,
			@RequestParam(value = "deviceNo", required = true) String deviceNo) {
		Device device = securityService.findByPhone(phone);
		if (device == null)
			return false;
		securityService.registerMerchant(id, device.getNo(), phone);
		return true;
	}

	@RequestMapping(value = "/merchant/qrCode", method = RequestMethod.POST)
	public @ResponseBody String modifyMerchantQrcode(@RequestParam(value = "id", required = true) Long id) {
		try {
			File dir = new File(env.getRequiredProperty("imageDir") + File.separator + "merchant");
			if (!dir.exists()) {
				dir.mkdirs();
			}
			String fileName = "" + id + "-qrCode";
			String content = "merchant=" + id;
			int width = 400; // 图像宽度
			int height = 400; // 图像高度
			String format = "png";// 图像类型

			Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
			hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
			BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);// 生成矩阵
			Path path = Paths.get(dir.getAbsolutePath(), fileName + ".png");
			MatrixToImageWriter.writeToPath(bitMatrix, format, path);// 输出图像
			securityService.updateMerchantQrCode(id, fileName);
			return fileName;
		} catch (Exception ex) {
			logger.info("upload file error...", ex);
			return "";
		}

	}

	@RequestMapping(value = "/merchant/openRange/{id}", method = RequestMethod.GET, produces = "application/json")
	public Merchant findMechantOpenRange(@PathVariable Long id) {
		Merchant merchant = securityService.findMerchantWithOpenRange(id);
		return merchant;
	}

	@RequestMapping(value = "/merchant/openRange/{id}", method = RequestMethod.POST, produces = "application/json")
	public Merchant modifyMechantOpenRange(@PathVariable Long id, @RequestBody Collection<OpenRange> openRanges) {
		Merchant merchant = securityService.updateOpenRange(id, openRanges);
		return merchant;
	}

	@RequestMapping(value = "/customer/merchant/{customerId}", method = RequestMethod.POST)
	public Set<Merchant> saveMerchantsOfCustomer(@PathVariable Long customerId, @RequestBody Set<Long> merchantIds) {
		return securityService.saveMerchantsOfCustomer(customerId, merchantIds);
	}

	@RequestMapping(value = "/customer/merchant/{customerId}", method = RequestMethod.GET, produces = "application/json")
	public Set<Merchant> findMechantsOfCustomer(@PathVariable Long customerId) {
		return securityService.findMerchantsOfCustomer(customerId);
	}

	@RequestMapping(value = "/customer/merchant/size/{customerId}", method = RequestMethod.GET, produces = "application/json")
	public Integer countMechantsOfCustomer(@PathVariable Long customerId) {
		Set<Merchant> merchants = securityService.findMerchantsOfCustomer(customerId);
		if (merchants == null) {
			return 0;
		} else {
			return merchants.size();
		}
	}

	@RequestMapping(value = "/merchant/name/{name}", method = RequestMethod.GET, produces = "application/json")
	public List<Merchant> findMechantByName(@PathVariable String name) {
		List<Merchant> merchants = securityService.findMerchantsByName(name);
		return merchants;
	}

	@CrossOrigin
	@RequestMapping(value = "/merchant/image", method = RequestMethod.POST)
	public @ResponseBody String uploadProductImage(@RequestParam("file") MultipartFile file,
			@RequestParam("merchantId") Long merchantId) {
		logger.info("upload file,merchantId: " + merchantId);
		if (!file.isEmpty()) {
			try {
				File dir = new File(env.getRequiredProperty("imageDir") + File.separator + "merchant");
				if (!dir.exists()) {
					dir.mkdirs();
				}

				String fileName = "" + merchantId;
				Thumbnails.of(file.getInputStream()).size(400, 400).outputFormat("png")
						.toFile(new File(dir, fileName + "-md"));
				Thumbnails.of(file.getInputStream()).size(50, 50).outputFormat("png")
						.toFile(new File(dir, fileName + "-xs"));
				Thumbnails.of(file.getInputStream()).size(62, 62).outputFormat("png")
						.toFile(new File(dir, fileName + "-sm"));
				securityService.updateMerchantImageSource(merchantId, fileName);
				return fileName;
			} catch (Exception ex) {
				logger.info("upload file error...", ex);
				return "";
			}
		} else {
			return "";
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/merchant/image/{fileName}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<InputStreamResource> downloadUserAvatarImage(@PathVariable String fileName)
			throws IOException {
		File image = new File(
				env.getRequiredProperty("imageDir") + File.separator + "merchant" + File.separator + fileName + ".png");
		InputStream in = new FileInputStream(image);
		return ResponseEntity.ok().contentLength(in.available()).contentType(MediaType.parseMediaType("image/png"))
				.body(new InputStreamResource(in));
	}

	@RequestMapping(value = "/device/{no}", method = RequestMethod.DELETE)
	public @ResponseBody Boolean deleteDevice(@PathVariable String no) {
		Device device = securityService.findByNo(no);
		if (device != null) {
			securityService.deleteDevice(device);
		}
		return true;
	}
}
