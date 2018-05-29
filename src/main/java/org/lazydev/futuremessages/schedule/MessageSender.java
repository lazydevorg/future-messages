package org.lazydev.futuremessages.schedule;

import brave.Tracer;
import org.lazydev.futuremessages.tracing.TracingHelper;
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
    private final TracingHelper tracingHelper;

    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public MessageSender(MessageChannel output, TracingHelper tracingHelper) {
        this.output = output;
        this.tracingHelper = tracingHelper;
    }

    public boolean send(FutureMessage futureMessage, MessageMetadata metadata) throws JobExecutionException {
        Message<Map<String, Object>> message = getMessage(futureMessage);
        Tracer.SpanInScope trace = tracingHelper.joinTrace(metadata.getTraceId(), metadata.getSampled());
        try (trace) {
            final boolean sent = output.send(message);
            log.info("Message {} sent", futureMessage.getId());
            return sent;
        } catch (MessageHandlingException e) {
            final JobExecutionException je = new JobExecutionException(e);
            je.setRefireImmediately(true);
            log.error("Message {} send error. Rescheduled!", futureMessage.getId());
            throw je;
        }
    }

    private Message<Map<String, Object>> getMessage(FutureMessage futureMessage) {
        return MessageBuilder.withPayload(futureMessage.getPayload())
                .setHeader("scheduleId", futureMessage.getId())
                .setHeader("destination", futureMessage.getDestination())
                .build();
    }
}
