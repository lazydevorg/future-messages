package org.lazydev.futuremessages.schedule;

import org.lazydev.futuremessages.api.Message;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class MessageScheduler {
    private static final Logger log = LoggerFactory.getLogger(MessageScheduler.class);
    private final Scheduler scheduler;

    @Autowired
    public MessageScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public ScheduledJob schedule(Message message) throws SchedulerException {
        JobDetail job = buildJob(message);
        Trigger trigger = buildTrigger(message);
        Date date = scheduler.scheduleJob(job, trigger);
        return new ScheduledJob(date, job);
    }

    private Trigger buildTrigger(Message message) {
        return TriggerBuilder.newTrigger()
                .startAt(Date.from(message.getStartAt()))
                .build();
    }

    private JobDetail buildJob(Message message) {
        return JobBuilder.newJob(MessageSenderJob.class)
                .usingJobData("payload", message.getPayload())
                .build();
    }
}
