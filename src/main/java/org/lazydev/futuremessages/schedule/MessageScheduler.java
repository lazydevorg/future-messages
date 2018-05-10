package org.lazydev.futuremessages.schedule;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper mapper;

    @Autowired
    public MessageScheduler(Scheduler scheduler, ObjectMapper mapper) throws SchedulerException {
        this.scheduler = scheduler;
        this.mapper = mapper;
        buildJob();
    }

    public ScheduledJob schedule(Message message) throws SchedulerException, JsonProcessingException {
        Trigger trigger = buildTrigger(message);
        Instant date = scheduler.scheduleJob(trigger).toInstant();
        return new ScheduledJob(date, trigger.getKey().getName());
    }

    private Trigger buildTrigger(Message message) throws JsonProcessingException {
        return TriggerBuilder.newTrigger()
                .forJob("messageSender")
                .usingJobData("payload", mapper.writeValueAsString(message.getPayload()))
                .startAt(Date.from(message.getStartAt()))
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
