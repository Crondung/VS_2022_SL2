import org.eclipse.paho.client.mqttv3.MqttException;

import javax.jms.JMSException;
import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Adapter {

    Adapter(String host, int port) throws JMSException, MqttException {
        OnMessage messageCallback = (broker, topic, message) -> {
            System.out.println(broker + ": topic=" + topic + " message: " + message);
            String severityString = getSeverityOfSyslogString(message);
            System.out.println("Severity: " + severityString);
            try {
                int sev = Integer.parseInt(severityString);
                if (sev <= 4){
                    DatagramSocket socket = new DatagramSocket();
                    InetAddress address = InetAddress.getByName(host);
                    byte[] messageBytes = message.getBytes();
                    DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length, address, port);
                    socket.send(packet);
                }
            } catch (Exception e) {
                System.out.println("Severity not a number");
                throw new RuntimeException(e);
            }
        };
        JMSConsumer consumer = new JMSConsumer("localhost", "JMS/test", messageCallback);
        Thread JMSThread = new Thread(consumer);
        JMSThread.start();
        MqttConsumer mqttAdapter = new MqttConsumer("tcp://broker.hivemq.com:1883", "clientX", "hsma/test/mqtt", messageCallback);
    }


    public static String getSeverityOfSyslogString(String syslogString) {
        Pattern pattern = Pattern.compile(">\\s*(\\d)");
        Matcher matcher = pattern.matcher(syslogString);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "9";
    }


    public static void main(String[] args) throws JMSException, MqttException {
        new Adapter("localhost", 3000);

    }
}
