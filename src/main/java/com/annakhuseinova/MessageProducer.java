package com.annakhuseinova;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Queue;
import javax.naming.InitialContext;

public class MessageProducer {

    public static void main(String[] args) throws Exception {
        InitialContext initialContext = new InitialContext();
        Queue requestQueue = (Queue) initialContext.lookup("queue/requestQueue");
        try(ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
            JMSContext context = connectionFactory.createContext(JMSContext.AUTO_ACKNOWLEDGE)){
            JMSProducer producer = context.createProducer();
            producer.send(requestQueue, "Message1");
        }
    }
}
