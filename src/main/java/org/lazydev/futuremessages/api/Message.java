package org.lazydev.futuremessages.api;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.Instant;

public class Message {
    @NotNull
    @Future
    private Instant startAt;
    private String payload;

    public Message() {
    }

    public Instant getStartAt() {
        return startAt;
    }

    public String getPayload() {
        return payload;
    }

    public void setStartAt(Instant startAt) {
        this.startAt = startAt;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "Message{" +
                "startAt=" + startAt +
                ", payload=" + payload +
                '}';
    }
}
