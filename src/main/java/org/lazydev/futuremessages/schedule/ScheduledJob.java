package org.lazydev.futuremessages.schedule;


import java.time.Instant;

public class ScheduledJob {
    private final Instant startAt;
    private final String triggerId;

    public ScheduledJob(Instant startAt, String triggerId) {
        this.startAt = startAt;
        this.triggerId = triggerId;
    }

    public Instant getStartAt() {
        return startAt;
    }

    public String getTriggerId() {
        return triggerId;
    }

    @Override
    public String toString() {
        return "ScheduledJob{" +
                "startAt=" + startAt +
                ", triggerId=" + triggerId +
                '}';
    }
}
