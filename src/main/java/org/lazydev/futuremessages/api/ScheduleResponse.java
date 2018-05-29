package org.lazydev.futuremessages.api;

import org.lazydev.futuremessages.schedule.ScheduledJob;

import java.time.Instant;

class ScheduleResponse {
    private final Instant start;
    private final String id;

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
}
