package org.lazydev.futuremessages.schedule;

import org.quartz.JobDetail;

import java.util.Date;

public class ScheduledJob {
    private final Date startAt;
    private final JobDetail jobDetail;

    ScheduledJob(Date startAt, JobDetail jobDetail) {
        this.startAt = startAt;
        this.jobDetail = jobDetail;
    }

    public Date getStartAt() {
        return startAt;
    }

    public JobDetail getJobDetail() {
        return jobDetail;
    }

    public String getJobId() {
        return jobDetail.getKey().getName();
    }

    @Override
    public String toString() {
        return "ScheduledJob{" +
                "startAt=" + startAt +
                ", jobDetail=" + jobDetail +
                '}';
    }
}
