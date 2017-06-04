package ticket.server.message;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;

import ticket.server.model.order.Cart;
import ticket.server.service.OrderService;

@Component
public class SendCartKafka {

	@Autowired
	KafkaProducer<String, String> kafkaProducer;

	@Autowired
	OrderService orderService;

	private final static Logger logger = LoggerFactory.getLogger(SendCartKafka.class);

	public SendCartKafka() {

	}

	public void send(Cart cart) {
		try {
			Cart jsonCart = orderService.findWithJsonData(cart.getId());

			if (jsonCart.getMerchant().getPrintNo() != null && !jsonCart.getMerchant().getPrintNo().equals("")) {
				String topic = jsonCart.getMerchant().getPrintNo();

				ObjectMapper mapper = new ObjectMapper();
				Hibernate5Module model = new Hibernate5Module();
				model.disable(Hibernate5Module.Feature.USE_TRANSIENT_ANNOTATION);
				mapper.registerModule(model);
				String cartJson = mapper.writeValueAsString(jsonCart);

				ProducerRecord<String, String> record = new ProducerRecord<>(topic, cartJson);
				kafkaProducer.send(record);
			}

		} catch (Exception ex) {
			logger.info("kafka cart json fail!", ex);
		}
	}
}
