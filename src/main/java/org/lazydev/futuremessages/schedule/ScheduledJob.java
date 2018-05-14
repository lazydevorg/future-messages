package org.lazydev.futuremessages.schedule;


import java.time.Instant;

public class ScheduledJob {
    private final Instant start;
    private final String triggerId;

    public ScheduledJob(Instant start, String triggerId) {
        this.start = start;
        this.triggerId = triggerId;
    }

    public Instant getStart() {
        return start;
    }

    public String getTriggerId() {
        return triggerId;
    }

    @Override
    public String toString() {
        return "ScheduledJob{" +
                "start=" + start +
                ", triggerId=" + triggerId +
                '}';
    }
}
