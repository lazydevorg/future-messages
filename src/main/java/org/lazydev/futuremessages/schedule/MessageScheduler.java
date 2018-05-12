package org.lazydev.futuremessages.schedule;

import org.lazydev.futuremessages.api.Message;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
public class MessageScheduler {
    private static final Logger log = LoggerFactory.getLogger(MessageScheduler.class);
    private final Scheduler scheduler;

    @Autowired
    public MessageScheduler(Scheduler scheduler) throws SchedulerException {
        this.scheduler = scheduler;
        buildJob();
    }

    public ScheduledJob schedule(Message message) throws SchedulerException {
        Trigger trigger = buildTrigger(message);
        Instant date = scheduler.scheduleJob(trigger).toInstant();
        return new ScheduledJob(date, trigger.getKey().getName());
    }

    private Trigger buildTrigger(Message message) {
        return TriggerBuilder.newTrigger()
                .forJob("messageSender")
                .usingJobData(new JobDataMap(message.getPayload()))
                //.startAt(Date.from(message.getStartAt()))
                .startAt(Date.from(Instant.now()))
                .build();
    }

    private JobDetail buildJob() throws SchedulerException {
        JobDetail job = JobBuilder.newJob(MessageSenderJob.class)
                .withIdentity("messageSender")
                .storeDurably()
                .build();
        scheduler.addJob(job, true);
        return job;
    }
}
