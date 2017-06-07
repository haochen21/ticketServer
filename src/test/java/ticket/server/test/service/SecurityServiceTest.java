package ticket.server.test.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import junit.framework.TestCase;
import ticket.server.config.HibernateJpaConfig;
import ticket.server.config.ServiceConfig;
import ticket.server.model.security.Customer;
import ticket.server.model.security.Merchant;
import ticket.server.model.security.NickNameEnCode;
import ticket.server.model.security.OpenRange;
import ticket.server.repository.security.CustomerRepository;
import ticket.server.service.SecurityService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { HibernateJpaConfig.class, ServiceConfig.class })
public class SecurityServiceTest {

	@Autowired
	SecurityService securityService;
	
	@Autowired
	CustomerRepository customerRepository;


	// @Test
	public void existsDeviceByNo() {
		Boolean result = securityService.existsDeviceByNo("xiaomian");
		TestCase.assertTrue(!result);
	}

	// @Test
	public void addOpenRange() {
		List<OpenRange> ranges = new ArrayList<>();

		OpenRange r1 = new OpenRange();

		LocalTime time1 = LocalTime.now();
		Instant instant1 = time1.atDate(LocalDate.of(2016, 7, 14)).atZone(ZoneId.systemDefault()).toInstant();
		Date beginTime = Date.from(instant1);

		LocalTime time2 = LocalTime.now();
		Instant instant2 = time2.plusMinutes(30).atDate(LocalDate.of(2016, 7, 14)).atZone(ZoneId.systemDefault())
				.toInstant();
		Date endTime = Date.from(instant2);

		r1.setBeginTime(beginTime);
		r1.setEndTime(endTime);

		OpenRange r2 = new OpenRange();
		LocalTime time3 = LocalTime.now();
		Instant instant3 = time3.plusHours(2).atDate(LocalDate.of(2016, 7, 14)).atZone(ZoneId.systemDefault())
				.toInstant();
		Date beginTime1 = Date.from(instant3);

		LocalTime time4 = LocalTime.now();
		Instant instant4 = time4.plusMinutes(30).plusHours(2).atDate(LocalDate.of(2016, 7, 14))
				.atZone(ZoneId.systemDefault()).toInstant();
		Date endTime1 = Date.from(instant4);

		r2.setBeginTime(beginTime1);
		r2.setEndTime(endTime1);

		ranges.add(r1);
		ranges.add(r2);

		securityService.updateOpenRange(new Long(416), ranges);
	}

	// @Test
	public void findWithOpenRanges() {
		Merchant m = securityService.findMerchantWithOpenRange(new Long(416));
		TestCase.assertEquals(m.getOpenRanges().size(), 2);
	}

	//@Test
	public void setMerchantsOfCustomer() {
		Set<Long> merchantIds = new HashSet<>();
		merchantIds.add(new Long(6));
		merchantIds.add(new Long(233));
		securityService.saveMerchantsOfCustomer(new Long(1), merchantIds);
		Set<Merchant> ms = securityService.findMerchantsOfCustomer(new Long(1));
		TestCase.assertEquals(ms.size(), 2);
	}
	
	@Test
	public void updateCustomerByNickName() {
//		List<Customer> customers = customerRepository.findAll();
//	    customers.stream().forEach(customer -> {
//	    	if(customer.getName()!= null && !customer.getLoginName().equals("") && !customer.getLoginName().equals("5Zut8J+OryDogJXlpLTwn463")){
//	    		//String name = NickNameEnCode.INSTANCE.decode(customer.getName());
//	    		String nickName = NickNameEnCode.INSTANCE.encode(customer.getLoginName());
//				customer.setName(nickName);
//				customer.setLoginName(nickName);
//	    	   securityService.updateCustomer(customer);
//	    	}
//	    });
		Customer customer = securityService.findCustomer(new Long(6362));
		String name = NickNameEnCode.INSTANCE.decode(customer.getName());
		System.out.println(name);
	}
}
