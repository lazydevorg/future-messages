package org.lazydev.futuremessages.schedule;

import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandlingException;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@EnableBinding(Source.class)
public class MessageSender {
    private static final Logger log = LoggerFactory.getLogger(MessageSender.class);
    private final MessageChannel output;

    @Autowired
    public MessageSender(MessageChannel output) {
        this.output = output;
    }

    public boolean send(FutureMessage futureMessage) throws JobExecutionException {
        Message<Map<String, Object>> message = MessageBuilder.withPayload(futureMessage.getPayload())
                .setHeader("scheduleId", futureMessage.getId())
                .setHeader("destination", futureMessage.getDestination())
                .build();
        log.debug("Message {}", message);
        try {
            return output.send(message);
        } catch (MessageHandlingException e) {
            final JobExecutionException je = new JobExecutionException(e);
            je.setRefireImmediately(true);
            log.debug("Message send error. Rescheduled!");
            throw je;
        }
    }
}
