package org.lazydev.futuremessages.schedule;

import org.lazydev.futuremessages.api.Message;
import org.quartz.*;
import org.quartz.utils.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.Date;

@Service
public class MessageScheduler {
    private static final Logger log = LoggerFactory.getLogger(MessageScheduler.class);
    public static final String MESSAGES_GROUP = "Futures";
    private static final String MESSAGE_SENDER_JOB_NAME = "MessageSender";
    private final Scheduler scheduler;

    @Autowired
    public MessageScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @PostConstruct
    public void init() throws SchedulerException {
        buildJob();
    }

    public ScheduledJob schedule(Message message) throws SchedulerException {
        Trigger trigger = buildTrigger(message);
        Instant date = scheduler.scheduleJob(trigger).toInstant();
        return new ScheduledJob(date, trigger.getKey().getName());
    }

    private Trigger buildTrigger(Message message) {
        return TriggerBuilder.newTrigger()
                .withIdentity(getTriggerKey())
                .forJob(MESSAGE_SENDER_JOB_NAME, MESSAGES_GROUP)
                .usingJobData(new JobDataMap(message.getPayload()))
                .startAt(Date.from(message.getStartAt()))
                //.startAt(Date.from(Instant.now()))
                .build();
    }

    private TriggerKey getTriggerKey() {
        return new TriggerKey(Key.createUniqueName(MESSAGES_GROUP), MESSAGES_GROUP);
    }

    private JobDetail buildJob() throws SchedulerException {
        JobDetail job = JobBuilder.newJob(MessageSenderJob.class)
                .withIdentity(MESSAGE_SENDER_JOB_NAME, MESSAGES_GROUP)
                .storeDurably()
                .build();
        scheduler.addJob(job, true);
        return job;
    }
}
