package org.lazydev.futuremessages.schedule;


import java.time.Instant;

public class ScheduledJob {
    private final Instant startAt;
    private final String jobId;

    public ScheduledJob(Instant startAt, String jobId) {
        this.startAt = startAt;
        this.jobId = jobId;
    }

    public Instant getStartAt() {
        return startAt;
    }

    public String getJobId() {
        return jobId;
    }

    @Override
    public String toString() {
        return "ScheduledJob{" +
                "startAt=" + startAt +
                ", jobId=" + jobId +
                '}';
    }
}
