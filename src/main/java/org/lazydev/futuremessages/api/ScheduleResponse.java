package org.lazydev.futuremessages.api;

import org.lazydev.futuremessages.schedule.ScheduledJob;

import java.time.Instant;

public class ScheduleResponse {
    private Instant start;
    private String id;

    ScheduleResponse(ScheduledJob job) {
        this.start = job.getStart();
        this.id = job.getTriggerId();
    }

    public Instant getStart() {
        return start;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "ScheduleResponse{" +
                "start=" + start +
                ", id='" + id + '\'' +
                '}';
    }
}
