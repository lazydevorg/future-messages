package org.lazydev.futuremessages.api;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Objects;

public class Message {
    @NotNull
    @Future
    private Instant startAt;
    private String payload;

    public Instant getStartAt() {
        return startAt;
    }

    public void setStartAt(Instant startAt) {
        this.startAt = startAt;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(startAt, message.startAt) &&
                Objects.equals(payload, message.payload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startAt, payload);
    }

    @Override
    public String toString() {
        return "Message{" +
                "startAt=" + startAt +
                ", payload=" + payload +
                '}';
    }
}
