package com.excelacom.century.apolloneoutbound.rest;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.excelacom.century.apolloneoutbound.bean.ErrorCodes;
import com.excelacom.century.apolloneoutbound.properties.ApolloNEQueueProperties;

import lombok.extern.log4j.Log4j2;

@RestController
@Log4j2
public class ApolloNEOutboundResource {

	
	@Autowired
	private RabbitTemplate customRabbitTemplate;
	
	@Autowired
	private ApolloNEQueueProperties apolloNEQueueProperties;

	@PostMapping(value = "#{apolloNEProperties.getOutboundapolloNeUrl()}", produces = MediaType.APPLICATION_JSON_VALUE)
	public String initiateApolloNEOutbound(@RequestParam String request) {
		log.info("initiateApolloNEOutbound response:: ", request);
		String responseString = "";
		try {
			Message message = MessageBuilder.withBody(request.getBytes())
					.setContentType(MediaType.APPLICATION_JSON_VALUE).build();
			// Message result = customRabbitTemplate.sendAndReceive(exchange.getName(),
			// queue.getName(), message);
			//Message result = rcsService.initiateApolloNEOutbound(message);
			Message result = customRabbitTemplate.sendAndReceive(apolloNEQueueProperties.getApolloNeOutboundServiceExchange(),
					apolloNEQueueProperties.getApolloNeOutboundServiceQueue(), message);
			
			responseString = new String(result.getBody());

		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0005+" : Exception{}", e);
		}
		log.info("Response initiateApolloNEOutbound:: ", responseString);
		return responseString;
	}

}
