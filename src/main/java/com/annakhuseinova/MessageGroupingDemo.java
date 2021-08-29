package com.annakhuseinova;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.naming.InitialContext;
import java.lang.IllegalStateException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageGroupingDemo {

    public static void main(String[] args) throws Exception{
        InitialContext initialContext = new InitialContext();
        Queue myQueue = (Queue) initialContext.lookup("queue/myQueue");
        Map<String, String> receivedMessages = new ConcurrentHashMap<>();
        try(ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
            JMSContext jmsContext = connectionFactory.createContext();
            JMSContext anotherJMSContext = connectionFactory.createContext()){
            JMSConsumer consumerOne = jmsContext.createConsumer(myQueue);
            JMSConsumer consumerTwo = anotherJMSContext.createConsumer(myQueue);
            consumerOne.setMessageListener(new MyListener("Consumer-1", receivedMessages));
            consumerTwo.setMessageListener(new MyListener("Consumer-2", receivedMessages));
            JMSProducer producer = jmsContext.createProducer();
            int count = 10;
            TextMessage[] messages = new TextMessage[count];
            for (int i = 0; i < count; i++) {
                messages[i] = jmsContext.createTextMessage("Group-0 message " + i);
                messages[i].setStringProperty("JMSXGroupID", "Group-0");
                producer.send(myQueue, messages[i]);
            }
            Thread.sleep(2000);
            for (TextMessage message: messages){
                if (!receivedMessages.get(message.getText()).equals("Consumer-1")){
                    throw new IllegalStateException("Group Message" + message.getText() + "has gone to the wrong receiver");
                }
            }
        }
    }
}

class MyListener implements MessageListener {

    private final String name;
    private final Map<String, String> receivedMessages;

    MyListener(String name, Map<String, String> receivedMessages){
        this.name = name;
        this.receivedMessages = receivedMessages;
    }

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {
            System.out.println("Message Received is : " + textMessage.getText());
            System.out.println("Listener Name " + name);
            receivedMessages.put(textMessage.getText(), name);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
