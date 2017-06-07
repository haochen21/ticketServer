package ticket.server.test.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

import ticket.server.config.HibernateJpaConfig;
import ticket.server.config.ServiceConfig;

@Configuration
@EnableScheduling
@Import({ HibernateJpaConfig.class, ServiceConfig.class })
public class OrderServiceConfig {

	public OrderServiceConfig() {

	}

	@Bean(name = "CartServiceTest")
	public OrderServiceTest createCartServiceTest() {
		OrderServiceTest cartTest = new OrderServiceTest();
		return cartTest;
	}
}
