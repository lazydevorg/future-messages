package org.lazydev.futuremessages.schedule;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.lazydev.futuremessages.api.Message;
import org.lazydev.futuremessages.tracing.TracingHelper;
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
    private final TracingHelper tracingHelper;

    @Autowired
    public MessageScheduler(Scheduler scheduler, ObjectMapper objectMapper, TracingHelper tracingHelper) {
        this.scheduler = scheduler;
        this.objectMapper = objectMapper;
        this.tracingHelper = tracingHelper;
    }

    @PostConstruct
    public void init() throws SchedulerException {
        buildJob();
    }

    public ScheduledJob schedule(Message message) throws SchedulerException, JsonProcessingException {
        Trigger trigger = buildTrigger(message);
        Instant startAt = scheduler.scheduleJob(trigger).toInstant();
        final String triggerId = trigger.getKey().getName();
        log.info("Job {} scheduled for {}", triggerId, startAt);
        return new ScheduledJob(startAt, triggerId);
    }

    private Trigger buildTrigger(Message message) throws JsonProcessingException {
        return TriggerBuilder.newTrigger()
                .withIdentity(getTriggerKey())
                .forJob(MESSAGE_SENDER_JOB_NAME, MESSAGES_GROUP)
                .usingJobData("destination", message.getDestination())
                .usingJobData("payload", objectMapper.writeValueAsString(message.getPayload()))
                .usingJobData("metadata", objectMapper.writeValueAsString(buildMetadata()))
                .startAt(Date.from(message.getStart()))
//                .startAt(Date.from(Instant.now()))
                .build();
    }

    private MessageMetadata buildMetadata() {
        MessageMetadata metadata = new MessageMetadata();
        metadata.setTraceId(tracingHelper.traceId());
        metadata.setSampled(tracingHelper.sampled());
        return metadata;
    }

    private TriggerKey getTriggerKey() {
        return new TriggerKey(Key.createUniqueName(MESSAGES_GROUP), MESSAGES_GROUP);
    }

    private void buildJob() throws SchedulerException {
        JobDetail job = JobBuilder.newJob(MessageSenderJob.class)
                .withIdentity(MESSAGE_SENDER_JOB_NAME, MESSAGES_GROUP)
                .storeDurably()
                .build();
        scheduler.addJob(job, true);
    }
}
