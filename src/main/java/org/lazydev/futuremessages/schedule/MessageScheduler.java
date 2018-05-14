package org.lazydev.futuremessages.schedule;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private static final String MESSAGES_GROUP = "Futures";
    private static final String MESSAGE_SENDER_JOB_NAME = "MessageSender";
    private final Scheduler scheduler;
    private final ObjectMapper objectMapper;

    @Autowired
    public MessageScheduler(Scheduler scheduler, ObjectMapper objectMapper) {
        this.scheduler = scheduler;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() throws SchedulerException {
        buildJob();
    }

    public ScheduledJob schedule(Message message) throws SchedulerException, JsonProcessingException {
        Trigger trigger = buildTrigger(message);
        Instant date = scheduler.scheduleJob(trigger).toInstant();
        return new ScheduledJob(date, trigger.getKey().getName());
    }

    private Trigger buildTrigger(Message message) throws JsonProcessingException {
        return TriggerBuilder.newTrigger()
                .withIdentity(getTriggerKey())
                .forJob(MESSAGE_SENDER_JOB_NAME, MESSAGES_GROUP)
                .usingJobData("destination", message.getDestination())
                .usingJobData("payload", objectMapper.writeValueAsString(message.getPayload()))
                .startAt(Date.from(message.getStart()))
//                .startAt(Date.from(Instant.now()))
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
