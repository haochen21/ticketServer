package ticket.server.message;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;

import ticket.server.model.order.Cart;
import ticket.server.service.OrderService;

@Component
public class SendCartJson {
    
	@Autowired
	OrderService orderService;
	
	private JmsTemplate cartJsonTemplate;

	private final static Logger logger = LoggerFactory.getLogger(SendCartJson.class);

	public JmsTemplate getCartJsonTemplate() {
		return cartJsonTemplate;
	}

	public void setCartJsonTemplate(JmsTemplate cartJsonTemplate) {
		this.cartJsonTemplate = cartJsonTemplate;
	}

	public SendCartJson() {

	}

	public void sendingCart(final Cart cart) {
		cartJsonTemplate.send(new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				try {
					ObjectMapper mapper = new ObjectMapper();
					Hibernate5Module model = new Hibernate5Module();
					model.disable(Hibernate5Module.Feature.USE_TRANSIENT_ANNOTATION);
					mapper.registerModule(model);
					Cart jsonCart = orderService.findWithJsonData(cart.getId());
					String cartJson = mapper.writeValueAsString(jsonCart);
					logger.info("cart json is: " + cartJson);
					TextMessage message = session.createTextMessage(cartJson);
					return message;
				} catch (Exception ex) {
					logger.info("parse cart json fail!", ex);
				}
				return null;
			}
		});
	}
}
