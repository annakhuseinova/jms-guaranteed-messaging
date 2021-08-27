package com.annakhuseinova;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Queue;
import javax.naming.InitialContext;

public class TransactionProducer {

    public static void main(String[] args) throws Exception {
        InitialContext initialContext = new InitialContext();
        Queue requestQueue = (Queue) initialContext.lookup("queue/requestQueue");
        try(ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
            JMSContext jmsContext = connectionFactory.createContext(JMSContext.SESSION_TRANSACTED)){
            JMSProducer producer = jmsContext.createProducer();
            producer.send(requestQueue, "Message1");
            producer.send(requestQueue, "Message2");
            jmsContext.commit();
        }
    }
}
