import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttConsumer implements MqttCallback {

    private MqttClient client;
    OnMessage callback;


    public MqttConsumer(String brokerUrl, String clientId, String topic, OnMessage callback) throws MqttException {
        this.callback = callback;
        client = new MqttClient(brokerUrl, clientId, new MemoryPersistence());

        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);

        client.connect(connOpts);

        client.setCallback(this);

        subscribe(topic);

        System.out.println("Started Mqtt Adapter");
    }

    public void subscribe(String topic) throws MqttException {
        client.subscribe(topic);
    }

    @Override
    public void connectionLost(Throwable cause) {
        System.out.println("Verbindung zum MQTT-Broker verloren");
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {

        String message = mqttMessage.toString();
        String sev = Adapter.getSeverityOfSyslogString(message);
        callback.onMessage("Mqtt", topic, message + " Sev= " + sev);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}