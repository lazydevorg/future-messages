package org.lazydev.futuremessages.schedule;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

@EnableBinding(Source.class)
class MessageSenderJob implements Job {
    private static final Logger log = LoggerFactory.getLogger(MessageSenderJob.class);

    @Autowired
    private MessageChannel output;

    @Override
    public void execute(JobExecutionContext context) {
        JobDataMap payload = context.getMergedJobDataMap();
        Message<JobDataMap> message = MessageBuilder.withPayload(payload)
                .setHeader("scheduleId", context.getTrigger().getKey().getName())
                .build();
        output.send(message);
        log.debug("Message {}", message);
    }
}
