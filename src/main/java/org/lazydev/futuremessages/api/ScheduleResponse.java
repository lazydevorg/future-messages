package org.lazydev.futuremessages.api;

import org.lazydev.futuremessages.schedule.ScheduledJob;

import java.util.Date;

public class ScheduleResponse {
    private Date startAt;
    private String jobId;

    ScheduleResponse(ScheduledJob job) {
        this.startAt = job.getStartAt();
        this.jobId = job.getJobId();
    }

    public Date getStartAt() {
        return startAt;
    }

    public String getJobId() {
        return jobId;
    }

    @Override
    public String toString() {
        return "ScheduleResponse{" +
                "startAt=" + startAt +
                ", jobId='" + jobId + '\'' +
                '}';
    }
}
