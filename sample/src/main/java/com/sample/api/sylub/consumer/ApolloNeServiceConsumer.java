/**
 * 
 */
package com.excelacom.century.apolloneoutbound.consumer;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.excelacom.century.apolloneoutbound.bean.ErrorCodes;
import com.excelacom.century.apolloneoutbound.properties.ApolloNEQueueProperties;
import com.excelacom.century.apolloneoutbound.service.ApolloNEOutboundService;

import lombok.extern.log4j.Log4j2;

/**
 * @author Dhayanand.B
 *
 */
@Log4j2
@Component
public class ApolloNeServiceConsumer {

	@Autowired
	private ApolloNEOutboundService apolloNEService;

	@Autowired
	private ApolloNEQueueProperties apolloNEQueueProperties;

	@RabbitListener(queues = "#{apolloNEQueueProperties.getApolloNeOutboundServiceQueue()}", containerFactory = "apolloNeoutboundServiceRabbitListenerContainer")
	public Message apolloNeSounthBoundCall(Message message) {
		log.debug("apolloNeSounthBoundCall::" + apolloNEQueueProperties.getApolloNeOutboundServiceQueue());
		Message result = null;
		try {
			result = apolloNEService.initiateApolloNEOutbound(message);
		} catch (Exception e) {
			log.error("ErrorCode : "+ErrorCodes.CECC0004+" : Exception ::", e);
		}
		return result;
	}
}
