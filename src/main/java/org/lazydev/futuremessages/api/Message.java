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
    private Instant startAt;
    private Map<String, Object> payload = new HashMap<>();

    public Instant getStartAt() {
        return startAt;
    }

    public void setStartAt(Instant startAt) {
        this.startAt = startAt;
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
                "startAt=" + startAt +
                ", payload=" + payload +
                '}';
    }
}
