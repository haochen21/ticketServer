package ticket.server.config;

import org.fusesource.stomp.jms.StompJmsConnectionFactory;
import org.fusesource.stomp.jms.StompJmsDestination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

import ticket.server.message.SendCartJson;

@Configuration
@PropertySource({ "classpath:/META-INF/ticket.properties" })
public class JmsConfig {

	@Autowired
	private Environment env;
	
	@Bean
	public StompJmsConnectionFactory stompConnectionFactory() {
		StompJmsConnectionFactory stompJmsConnectionFactory = new StompJmsConnectionFactory();
		stompJmsConnectionFactory.setBrokerURI(env.getRequiredProperty("stompBrokerUrl"));
		return stompJmsConnectionFactory;
	}

	@Bean
	public CachingConnectionFactory stompCachingConnectionFactory() {
		CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
		cachingConnectionFactory.setTargetConnectionFactory(stompConnectionFactory());
		cachingConnectionFactory.setSessionCacheSize(1);
		return cachingConnectionFactory;
	}

	@Bean
	public JmsTemplate cartJsonTemplate() {
		JmsTemplate jmsTemplate = new JmsTemplate();
		jmsTemplate.setDefaultDestination(new StompJmsDestination(env.getRequiredProperty("cartJsonDestination")));
		jmsTemplate.setConnectionFactory(stompCachingConnectionFactory());
		jmsTemplate.setTimeToLive(Long.parseLong(env.getRequiredProperty("timeToLive")));
		return jmsTemplate;
	}
	
	@Bean
	public SendCartJson createSendCartJson() {
		SendCartJson sendCartJson = new SendCartJson();
		sendCartJson.setCartJsonTemplate(cartJsonTemplate());
		return sendCartJson;
	}

}
