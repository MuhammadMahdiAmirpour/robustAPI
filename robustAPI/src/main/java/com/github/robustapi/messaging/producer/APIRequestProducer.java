package com.github.robustapi.messaging.producer;

import com.github.robustapi.config.RabbitConfig;
import com.github.robustapi.request.APIRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class APIRequestProducer {

	private final RabbitTemplate rabbitTemplate;

	public APIRequestProducer(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	public void sendRequest(APIRequest apiRequest) {
		// Send the APIRequest to the RabbitMQ exchange with the specified routing key
		rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_NAME, RabbitConfig.ROUTING_KEY, apiRequest);
		System.out.println("Sent request: " + apiRequest);
	}
}
