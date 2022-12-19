public interface OnMessage {
    void onMessage(String broker, String topic, String message);
}
