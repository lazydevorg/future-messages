package org.lazydev.futuremessages.schedule;

import java.util.Map;

class FutureMessage {
    private String id;
    private String destination;
    private Map<String, Object> payload;

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
}
