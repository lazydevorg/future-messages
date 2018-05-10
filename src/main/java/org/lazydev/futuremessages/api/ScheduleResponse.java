package org.lazydev.futuremessages.api;

import org.lazydev.futuremessages.schedule.ScheduledJob;

import java.time.Instant;

public class ScheduleResponse {
    private Instant startAt;
    private String id;

    ScheduleResponse(ScheduledJob job) {
        this.startAt = job.getStartAt();
        this.id = job.getTriggerId();
    }

    public Instant getStartAt() {
        return startAt;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "ScheduleResponse{" +
                "startAt=" + startAt +
                ", id='" + id + '\'' +
                '}';
    }
}
