package org.lazydev.futuremessages.schedule;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class FutureMessage {
    private final String id;
    private final String destination;
    private final Map<String, Object> payload;
    private final Map<String, Object> metadata = new HashMap<>();

    public FutureMessage(String id, String destination, Map<String, Object> payload) {
        this.id = id;
        this.destination = destination;
        this.payload = payload;
    }

    public String getId() {
        return id;
    }

    public String getDestination() {
        return destination;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public <T> T getMetadata(String key, Class<T> clazz) {
        return (T) metadata.get(key);
    }

    public <T> T putMetadata(String key, T data) {
        return (T) metadata.put(key, data);
    }
}
