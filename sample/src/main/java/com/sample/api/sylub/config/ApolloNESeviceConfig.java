package com.excelacom.century.apolloneoutbound.config;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory.CacheMode;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.ConditionalRejectingErrorHandler;
import org.springframework.amqp.rabbit.listener.FatalExceptionStrategy;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.util.ErrorHandler;

import com.excelacom.century.apolloneoutbound.exception.CustomFatalExceptionStrategy;
import com.excelacom.century.apolloneoutbound.properties.ApolloNEQueueProperties;
import com.excelacom.century.apolloneoutbound.properties.ApolloNEServiceProperties;


@Configuration
@EnableRabbit
@Import(value = ApolloNEServiceProperties.class)
public class ApolloNESeviceConfig {

	@Value("${spring.application.dlqExchange}")
	private String dlqExchange;

	
	@Value("${spring.application.dlqueue}")
	private String dlqueue;

	@Value("${spring.rabbitmq.host}")
	private String host;

	@Value("${spring.rabbitmq.port}")
	private String port;

	@Value("${spring.rabbitmq.username}")
	private String username;

	@Value("${spring.rabbitmq.password}")
	private String password;

	@Value("${spring.rabbitmq.listener.simple.outbound.concurrency}")
	private String concurrentConsumers;

	@Value("${spring.rabbitmq.listener.simple.outbound.max-concurrency}")
	private String maxConcurrentConsumers;

	@Autowired
	private ApolloNEQueueProperties apolloNEQueueProperties;

	@Bean
	public DirectExchange deadLetterExchange() {
		return new DirectExchange(dlqExchange);
	}

	
	@Bean
	public Queue dlq() {
		return QueueBuilder.durable(dlqueue).build();
	}

	@Bean
	Binding DLQbinding() {
		return BindingBuilder.bind(dlq()).to(deadLetterExchange()).with(dlqueue);
	}

	@Bean
	public MessageConverter jsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}
	
	@Bean
	public Queue searchEnvWaitQueue() {
		return QueueBuilder.durable(apolloNEQueueProperties.getSearchEnvironmentWaitQueue())
				.withArgument("x-dead-letter-exchange", dlqExchange).withArgument("x-dead-letter-routing-key", dlqueue)
				.build();
	}

	@Bean
	public DirectExchange searchEnvWaitExchange() {
		return new DirectExchange(apolloNEQueueProperties.getSearchEnvironmentWaitExchange());
	}

	@Bean
	Binding searchEnvWaitBinding() {
		return BindingBuilder.bind(searchEnvWaitQueue()).to(searchEnvWaitExchange())
				.with(apolloNEQueueProperties.getSearchEnvironmentWaitQueue());
	}

	
	
	@Bean
	public Queue apolloNeoutboundServiceQueue() {
		return QueueBuilder.durable(apolloNEQueueProperties.getApolloNeOutboundServiceQueue())
				.withArgument("x-dead-letter-exchange", dlqExchange).withArgument("x-dead-letter-routing-key", dlqueue)
				.build();
	}

	@Bean
	public DirectExchange apolloNeOutboundServiceExchange() {
		return new DirectExchange(apolloNEQueueProperties.getApolloNeOutboundServiceExchange());
	}

	@Bean
	Binding apolloNeoutboundServiceBinding() {
		return BindingBuilder.bind(apolloNeoutboundServiceQueue()).to(apolloNeOutboundServiceExchange())
				.with(apolloNEQueueProperties.getApolloNeOutboundServiceQueue());
	}

	@Bean(name = "apolloNeoutboundServiceConnectionFactory")
	public ConnectionFactory apolloNeoutboundServicerabbitMQConnectionFactory() {
		CachingConnectionFactory apolloNeoutboundServiceconnectionFactory = new CachingConnectionFactory(host);
		apolloNeoutboundServiceconnectionFactory.setPort(Integer.parseInt(port));
		apolloNeoutboundServiceconnectionFactory.setUsername(username);
		apolloNeoutboundServiceconnectionFactory.setPassword(password);
		apolloNeoutboundServiceconnectionFactory.setConnectionTimeout(apolloNEQueueProperties.getConnectionTimeOut());
		return apolloNeoutboundServiceconnectionFactory;
	}

	@Bean()
	RabbitAdmin apolloNeoutboundServiceRabbitAdmin() {
		return new RabbitAdmin(apolloNeoutboundServicerabbitMQConnectionFactory());
	}

	@Bean("apolloNeoutboundServiceRabbitListenerContainer")	
	public SimpleRabbitListenerContainerFactory apolloNeoutboundServiceContainerFactory() {
		SimpleRabbitListenerContainerFactory apolloNeoutboundServicefactory = new SimpleRabbitListenerContainerFactory();
		apolloNeoutboundServicefactory.setConcurrentConsumers(Integer.parseInt(concurrentConsumers));
		apolloNeoutboundServicefactory.setMaxConcurrentConsumers(Integer.parseInt(maxConcurrentConsumers));
		apolloNeoutboundServicefactory.setPrefetchCount(1);
		apolloNeoutboundServicefactory.setReceiveTimeout(apolloNEQueueProperties.getReceiveTimeOut());
		apolloNeoutboundServicefactory.setConnectionFactory(apolloNeoutboundServicerabbitMQConnectionFactory());
		apolloNeoutboundServicefactory.setErrorHandler(errorHandler());
		return apolloNeoutboundServicefactory;
	}

	@Bean
	public Queue apolloNeSendClientQueue() {
		return QueueBuilder.durable(apolloNEQueueProperties.getApolloNeSendClientQueue())
				.withArgument("x-dead-letter-exchange", apolloNEQueueProperties.getApolloNeRetryExchange())
				.withArgument("x-dead-letter-routing-key", apolloNEQueueProperties.getApolloNeRetryRouter()).build();
	}

	
	

	@Bean
	public DirectExchange apolloNeSendClientExchange() {
		return new DirectExchange(apolloNEQueueProperties.getApolloNeSendClientExchange());
	}

	

	@Bean
	Binding apolloNeSendClientbinding() {
		return BindingBuilder.bind(apolloNeSendClientQueue()).to(apolloNeSendClientExchange())
				.with(apolloNEQueueProperties.getApolloNeSendClientQueue());
	}


	@Bean(name = "apolloNeSendClientConnectionFactory")
	public ConnectionFactory apolloNeSendClientConnectionFactory() {
		CachingConnectionFactory apolloNeSendClientConnectionFactory = new CachingConnectionFactory(host);
		apolloNeSendClientConnectionFactory.setPort(Integer.parseInt(port));
		apolloNeSendClientConnectionFactory.setUsername(username);
		apolloNeSendClientConnectionFactory.setPassword(password);
		apolloNeSendClientConnectionFactory.setCacheMode(CacheMode.CHANNEL);
		apolloNeSendClientConnectionFactory.setConnectionTimeout(apolloNEQueueProperties.getConnectionTimeOut());
		return apolloNeSendClientConnectionFactory;
	}

	@Bean()
	RabbitAdmin apolloNeSendClientRabbitAdmin() {
		return new RabbitAdmin(apolloNeSendClientConnectionFactory());
	}

	@Bean(name = "apolloNeSendClientContainer")
	public SimpleRabbitListenerContainerFactory apolloNeSendClientContainerFactory() {
		SimpleRabbitListenerContainerFactory apolloNeSendClientFactory = new SimpleRabbitListenerContainerFactory();
		apolloNeSendClientFactory.setConcurrentConsumers(Integer.parseInt(concurrentConsumers));
		apolloNeSendClientFactory.setMaxConcurrentConsumers(Integer.parseInt(maxConcurrentConsumers));
		apolloNeSendClientFactory.setPrefetchCount(1);
		apolloNeSendClientFactory.setReceiveTimeout(apolloNEQueueProperties.getReceiveTimeOut());
		apolloNeSendClientFactory.setConnectionFactory(apolloNeSendClientConnectionFactory());
		apolloNeSendClientFactory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
		//apolloNeSendClientFactory.setErrorHandler(errorHandler());
		return apolloNeSendClientFactory;
	}

	
	
	@Bean
	public Queue apolloNeRetryQueue() {
		return QueueBuilder.durable(apolloNEQueueProperties.getApolloNeRetryQueue()).withArgument("x-message-ttl", 4000)
				.withArgument("x-dead-letter-exchange", apolloNEQueueProperties.getApolloNeSendClientExchange())
				.withArgument("x-dead-letter-routing-key", apolloNEQueueProperties.getApolloNeSendClientQueue()).build();
	}


	@Bean
	public DirectExchange apolloNeRetryExchange() {
		return new DirectExchange(apolloNEQueueProperties.getApolloNeRetryExchange());
	}

	@Bean
	Binding apolloNeRetrybinding() {
		return BindingBuilder.bind(apolloNeRetryQueue()).to(apolloNeRetryExchange())
				.with(apolloNEQueueProperties.getApolloNeRetryRouter());
	}
	
	@Bean
	public Queue apolloNeErrorQueue() {
		return QueueBuilder.durable(apolloNEQueueProperties.getApolloNeErrorQueue()).build();
	}
	
	
	@Bean
	public DirectExchange apolloNeErrorExchange() {
		return new DirectExchange(apolloNEQueueProperties.getApolloNeErrorExchange());
	}
	

	@Bean
	Binding apolloNeErrorbinding() {
		return BindingBuilder.bind(apolloNeErrorQueue()).to(apolloNeErrorExchange())
				.with(apolloNEQueueProperties.getApolloNeErrorQueue());
	}

	@Bean(name = "ConnectionFactory")
	@Primary
	public ConnectionFactory rabbitMQConnectionFactory() {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host);
		connectionFactory.setPort(Integer.parseInt(port));
		connectionFactory.setUsername(username);
		connectionFactory.setPassword(password);
		connectionFactory.setConnectionTimeout(apolloNEQueueProperties.getConnectionTimeOut());
		// connectionFactory.setConnectionCacheSize(25);
		// connectionFactory.setExecutor(getAsyncExecutor());
		return connectionFactory;
	}

	@Bean("customSimpleRabbitListenerContainer")
	public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory() {
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		factory.setConnectionFactory(rabbitMQConnectionFactory());
		//factory.setConcurrentConsumers(200);
		//factory.setMaxConcurrentConsumers(250);
		factory.setReceiveTimeout(apolloNEQueueProperties.getReceiveTimeOut());
		return factory;
	}
	
	@Bean("customRabbitTemplate")
	@Primary
	public RabbitTemplate customRabbitTemplate() {
		RabbitTemplate rabbitTemplate = new RabbitTemplate();
		rabbitTemplate.setReplyTimeout(60000L);
		rabbitTemplate.setConnectionFactory(rabbitMQConnectionFactory());
		rabbitTemplate.setMessageConverter(jsonMessageConverter());
		return rabbitTemplate;
	}

	
	@Bean
	public ErrorHandler errorHandler() {
		return new ConditionalRejectingErrorHandler(customExceptionStrategy());
	}

	@Bean
	FatalExceptionStrategy customExceptionStrategy() {
		return new CustomFatalExceptionStrategy();
	}

}
