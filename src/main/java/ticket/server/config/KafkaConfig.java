package ticket.server.config;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource({ "classpath:/META-INF/ticket.properties" })
public class KafkaConfig {

	@Autowired
	private Environment env;	
	
	@Bean(destroyMethod = "close")
	public KafkaProducer<String,String> kafkaProducer() {
		KafkaProducer<String,String> producer = new KafkaProducer<String, String>(producerProperties());
		return producer;
	}

	private Properties producerProperties() {
		Properties kafkaProps = new Properties();
		kafkaProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,env.getRequiredProperty("kafkaServer"));
		kafkaProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		kafkaProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		return kafkaProps;
	}
}
