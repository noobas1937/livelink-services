package com.youhaoxi.livelink.gateway.dispatch.mq;

import com.rabbitmq.client.*;
import com.youhaoxi.livelink.gateway.common.util.NetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class RabbitConsumer {
    private static final Logger logger = LoggerFactory.getLogger(RabbitConsumer.class);


    private Channel channel = RabbitConnectionManager.getInstance().getNewChannel();
    private String EXCHANGE_NAME ;
    private String queueName;

    private Processor processor;


    public RabbitConsumer(String exchange, BuiltinExchangeType type){
        try {
            logger.debug("初始化RabbitConsumer exchange:{} type:{}",exchange,type);
            EXCHANGE_NAME = exchange;
            channel.exchangeDeclare(exchange, type);
            queueName = channel.queueDeclare().getQueue();
        } catch (IOException e) {
            logger.error("RabbitProducer error:"+e.getMessage(),e);
        }
    }

    /**
     * routeKey就是本机hostip
     * @param routekey
     */
    public  void consume(String routekey){
        try {

            channel.queueBind(queueName, EXCHANGE_NAME, routekey);
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope,
                                           AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    logger.debug(" [x] Received routeKey:'" + envelope.getRoutingKey() + "' message:'" + message + "'");

                    //下发给用户
                    processor.process(body);

                }
            };
            channel.basicConsume(queueName, true, consumer);

        } catch (IOException e) {
            logger.error("publish error:"+e.getMessage(),e);
        }
    }


    public static void main(String[] argv) throws Exception {
        String host = NetUtils.getLocalAddress().getHostAddress();
        //new RabbitConsumer().consume(host);
    }

    public Processor getProcessor() {
        return processor;
    }

    public RabbitConsumer setProcessor(Processor processor) {
        this.processor = processor;
        return this;
    }
}
