package ticket.server.service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import ticket.server.model.security.Customer;
import ticket.server.model.security.CustomerLogin;
import ticket.server.model.security.Device;
import ticket.server.model.security.Merchant;
import ticket.server.model.security.MerchantLogin;
import ticket.server.model.security.OpenRange;

public interface SecurityService {

	Customer findCustomerByOpenId(String openId);

	Merchant findMerchantByOpenId(String openId);

	Boolean existsCustomerByOpenId(String openId);

	Boolean existsMerchantByOpenId(String openId);

	Merchant saveMerchant(Merchant merchant);

	Merchant updateMerchant(Merchant merchant);

	Merchant findMerchant(Long merchantId);

	Merchant findMerchantWithOpenRange(Long id);

	Merchant findMerchantByDeviceNo(String deviceNo);

	void updateMerchantOpen(Long id, Boolean open);

	void updateMerchantImageSource(Long id, String imageSource);

	void updateMerchantQrCode(Long id, String qrCode);

	void registerMerchant(Long id, String deviceNo, String phone);

	Merchant updateOpenRange(Long merchantId, Collection<OpenRange> ranges);

	CustomerLogin customerLogin(String loginName, String password);

	Customer saveCustomer(Customer customer);

	Customer updateCustomer(Customer customer);

	Customer findCustomer(Long customerId);

	Customer findCustomerByCardNo(String cardNo);

	Customer findCustomerByPhone(String phone);

	void updateCustomerPhone(Long id, String phone);

	MerchantLogin merchantLogin(String loginName, String password);

	void modifyCustomerPassword(Long id, String password);

	void modifyMerchantPassword(Long id, String password);

	Boolean existsCustomerByLoginName(String loginName);

	Boolean existsMerchantByLoginName(String loginName);

	Device createDevice(Device device);

	void deleteDevice(Device device);

	Device findByNo(String no);

	Device findByPhone(String phone);

	Boolean existsDeviceByNo(String no);

	Boolean existsDeviceByPhone(String phone);

	Boolean existsByCardNo(String cardNo);

	Boolean existsCustomerByPhone(String phone);

	Boolean existsMerchantByPhone(String phone);

	Set<Merchant> saveMerchantsOfCustomer(Long customerId, Set<Long> merchantIds);

	Set<Merchant> findMerchantsOfCustomer(Long customerId);

	List<Merchant> findMerchantsByName(String name);
}
