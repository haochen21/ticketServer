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
import ticket.server.model.order.CartStatus;
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

			ObjectMapper mapper = new ObjectMapper();
			Hibernate5Module model = new Hibernate5Module();
			model.disable(Hibernate5Module.Feature.USE_TRANSIENT_ANNOTATION);
			mapper.registerModule(model);
			String cartJson = mapper.writeValueAsString(jsonCart);

			String topic = "order";
			ProducerRecord<String, String> record = new ProducerRecord<>(topic, cartJson);
			kafkaProducer.send(record);

			if (jsonCart.getMerchant().getPrintNo() != null && !jsonCart.getMerchant().getPrintNo().equals("") && cart.getStatus() == CartStatus.CONFIRMED) {
				String[] printNos = jsonCart.getMerchant().getPrintNo().split(",");
				for(String printNo : printNos){
					String printTopic = "print-" + printNo;
					logger.info("printNo is: {}",printNo);
					ProducerRecord<String, String> printRecord = new ProducerRecord<>(printTopic, cartJson);
					kafkaProducer.send(printRecord);
				}
			}
		} catch (Exception ex) {
			logger.info("kafka cart json fail!", ex);
		}
	}
	
	public void manualPrint(Long cartId) {
		try {
			Cart jsonCart = orderService.findWithJsonData(cartId);

			ObjectMapper mapper = new ObjectMapper();
			Hibernate5Module model = new Hibernate5Module();
			model.disable(Hibernate5Module.Feature.USE_TRANSIENT_ANNOTATION);
			mapper.registerModule(model);
			String cartJson = mapper.writeValueAsString(jsonCart);

			if (jsonCart.getMerchant().getPrintNo() != null && !jsonCart.getMerchant().getPrintNo().equals("") && jsonCart.getStatus() == CartStatus.CONFIRMED) {
				String[] printNos = jsonCart.getMerchant().getPrintNo().split(",");
				for(String printNo : printNos){
					String printTopic = "manualprint-" + printNo;
					ProducerRecord<String, String> printRecord = new ProducerRecord<>(printTopic, cartJson);
					kafkaProducer.send(printRecord);
					logger.info("manual print topic {},cart {}",printTopic,jsonCart.getId());
				}	
			}
		} catch (Exception ex) {
			logger.info("kafka cart json fail!", ex);
		}
	}
}
