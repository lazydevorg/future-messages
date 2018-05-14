package org.lazydev.futuremessages.api;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class Message {
    @NotNull
    @Future
    private Instant start;
    @NotNull
    private String destination;
    private Map<String, Object> payload = new HashMap<>();

    public Instant getStart() {
        return start;
    }

    public void setStart(Instant start) {
        this.start = start;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    @JsonAnyGetter
    public Map<String, Object> getPayload() {
        return payload;
    }

    @JsonAnySetter
    public void setPayloadData(String key, Object value) {
        payload.put(key, value);
    }

    @Override
    public String toString() {
        return "Message{" +
                "start=" + start +
                ", payload=" + payload +
                '}';
    }
}
