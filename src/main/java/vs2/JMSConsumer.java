import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class JMSConsumer implements Runnable{

    String JMS_PORT = "61616";
    String address;
    String topic;

    OnMessage callback;

    JMSConsumer(String address, String topic, OnMessage callback){
        this.address = address;
        this.topic = topic;
        this.callback = callback;
        System.out.println("Started JMS Consumer");
    }

    @Override
    public void run() {
        // Erstelle eine Verbindung zum ActiveMQ-Broker
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://" + address + ":" + JMS_PORT);
        Connection connection = null;
        try {
            connection = connectionFactory.createConnection();

        connection.start();

        // Erstelle eine Session und ein Destination-Objekt (Queue oder Topic)
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue(this.topic);

        // Erstelle einen MessageConsumer für das Destination-Objekt
        MessageConsumer consumer = session.createConsumer(destination);

        // Empfange Nachrichten vom Consumer
        while (true) {
            Message message = consumer.receive();
            if (message instanceof TextMessage) {
                String text = ((TextMessage) message).getText();
                callback.onMessage("JMS", this.topic, text);
            } else {
                System.out.println("Empfangene Nachricht: " + message);
            }
        }
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }

        // Schließe die Verbindung
        //connection.close();
    }
}

