package com.lzq.jesper_seckill.rabbitMQ;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.amqp.rabbit.connection.CachingConnectionFactory.ConfirmType.CORRELATED;

@Configuration
public class rabbitMQConfig {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${spring.rabbitmq.host}")
    private String host;
    @Value("${spring.rabbitmq.port}")
    private int port;
    @Value("${spring.rabbitmq.username}")
    private String username;
    @Value("${spring.rabbitmq.password}")
    private String password;
    public static final String BUSINESS_EXCHANGE_NAME = "rabbitmq.dead.letter.business.exchange";
    public static final String BUSINESS_QUEUEA_NAME = "rabbitmq.dead.letter.business.queueA";
    public static final String BUSINESS_QUEUEB_NAME = "rabbitmq.dead.letter.business.queueB";
    public static final String BUSINESS_QUEUEA_ROUTING_KEY = "rabbitmq.dead.letter.business.queueA.routingkey";
    public static final String BUSINESS_QUEUEB_ROUTING_KEY = "rabbitmq.dead.letter.business.queueB.routingkey";

    public static final String DEAD_LETTER_EXCHANGE = "rabbitmq.dead.letter.deadletter.exchange";
    public static final String DEAD_LETTER_QUEUE_NAME = "rabbitmq.dead.letter.deadletter.queue";
    public static final String DEAD_LETTER_QUEUE_ROUTING_KEY = "rabbitmq.dead.letter.deadletter.queue.routingkey";

    //???????????????RabbitAdmin??????
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        // ??????????????? true???spring ???????????? RabbitAdmin ?????????
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }

    @Bean("connectionFactory2")
    public ConnectionFactory connectionFactory(){
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host, port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost("/");
        //???????????????
        connectionFactory.setPublisherConfirmType(CORRELATED);
        return connectionFactory;
    }

    @Bean("rabbitTemplate2")
    public RabbitTemplate  rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        return rabbitTemplate;
    }
    // ????????????Exchange
    @Bean("businessExchange")
    public DirectExchange businessExchange(){
        return new DirectExchange(BUSINESS_EXCHANGE_NAME);
    }
    // ??????????????????A
    @Bean("businessQueueA")
    public Queue businessQueueA(){
        return new Queue(BUSINESS_QUEUEA_NAME,true,false,false);
    }
    // ??????????????????B,??????????????????
    @Bean("businessQueueB")
    public Queue businessQueueB(){
        Map<String,Object> map = new HashMap<>();
        //x-dead-letter-exchange    ????????????????????????????????????????????????
        map.put("x-dead-letter-exchange",DEAD_LETTER_EXCHANGE);
        //x-dead-letter-routing-key  ???????????????????????????????????????key
        map.put("x-dead-letter-routing-key",DEAD_LETTER_QUEUE_ROUTING_KEY);
        //???????????????ttl??????-?????????60???
        map.put("x-message-ttl", 60000);
        return new Queue(BUSINESS_QUEUEB_NAME,true,false,false,map);
    }
    @Bean
    public Binding businessBindingA(@Qualifier("businessQueueA") Queue queue, @Qualifier("businessExchange") DirectExchange directExchange){
        return BindingBuilder.bind(queue).to(directExchange).with(BUSINESS_QUEUEA_ROUTING_KEY);
    }
    @Bean
    public Binding businessBindingB(@Qualifier("businessQueueB") Queue queue, @Qualifier("businessExchange") DirectExchange directExchange){
        return BindingBuilder.bind(queue).to(directExchange).with(BUSINESS_QUEUEB_ROUTING_KEY);
    }
    // ????????????Exchange
    @Bean("deadLetterExchange")
    public DirectExchange deadLetterExchange(){
        return new DirectExchange(DEAD_LETTER_EXCHANGE);
    }
    // ??????????????????
    @Bean("deadLetterQueue")
    public Queue deadLetterQueue(){
        return new Queue(DEAD_LETTER_QUEUE_NAME);
    }
    // ??????????????????????????????
    @Bean
    public Binding deadLetterBinding(@Qualifier("deadLetterQueue") Queue queue,@Qualifier("deadLetterExchange") DirectExchange directExchange){
        return BindingBuilder.bind(queue).to(directExchange).with(DEAD_LETTER_QUEUE_ROUTING_KEY);
    }
}
