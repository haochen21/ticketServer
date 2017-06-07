package ticket.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import ticket.server.message.SendCartJsonExecutor;
import ticket.server.message.SendCartKafka;
import ticket.server.process.NeedPayCarMonitor;
import ticket.server.process.NoNeedPayCartMonitor;

@Configuration
@PropertySource({ "classpath:/META-INF/ticket.properties" })
@ComponentScan({ "ticket.server.service" })
public class ServiceConfig {

	@Autowired
	private Environment env;
	
	@Autowired
	SendCartKafka sendCartKafka;
	
	@Bean
	public NeedPayCarMonitor createNeedPayCarMonitor() {
		int processNum = Integer.parseInt(env.getRequiredProperty("needPayProcessNum"));
		NeedPayCarMonitor monitor = new NeedPayCarMonitor(processNum);
		return monitor;
	}

	@Bean
	public NoNeedPayCartMonitor createNoNeedPayCartMonitor() {
		int processNum = Integer.parseInt(env.getRequiredProperty("noNeedPayProcessNum"));
		NoNeedPayCartMonitor monitor = new NoNeedPayCartMonitor(processNum);
		return monitor;
	}	

	@Bean
	public SendCartJsonExecutor createSendCartJsonExecutor() {
		SendCartJsonExecutor executor = new SendCartJsonExecutor();
		executor.setSendCartKafka(sendCartKafka);
		return executor;
	}
	
	@Bean
	public SendCartKafka createSendCartKafka() {
		SendCartKafka sendCartKafka = new SendCartKafka();
		return sendCartKafka;
	}
}
