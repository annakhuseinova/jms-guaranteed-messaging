package com.annakhuseinova;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.naming.InitialContext;

public class TransactionConsumer {

    public static void main(String[] args) throws Exception {
        InitialContext initialContext = new InitialContext();
        Queue requestQueue = (Queue) initialContext.lookup("queue/requestQueue");
        try(ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
            JMSContext context = connectionFactory.createContext(JMSContext.SESSION_TRANSACTED)){
            JMSConsumer jmsConsumer = context.createConsumer(requestQueue);
            TextMessage message = (TextMessage) jmsConsumer.receive();
            System.out.println(message.getText());
            context.commit();
        }
    }
}
