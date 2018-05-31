package org.lazydev.futuremessages.schedule;

import org.lazydev.futuremessages.interceptors.InterceptorException;
import org.lazydev.futuremessages.interceptors.SchedulerInterceptorManager;
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

import static net.logstash.logback.argument.StructuredArguments.value;

@Service
@EnableBinding(Source.class)
public class MessageSender {
    private static final Logger log = LoggerFactory.getLogger(MessageSender.class);
    private final MessageChannel output;
    private final SchedulerInterceptorManager interceptorManager;

    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public MessageSender(MessageChannel output, SchedulerInterceptorManager interceptorManager) {
        this.output = output;
        this.interceptorManager = interceptorManager;
    }

    public boolean send(FutureMessage futureMessage) throws JobExecutionException {
        Message<Map<String, Object>> message = buildMessage(futureMessage);
        boolean sent = false;
        try {
            runBeforeSendInterceptors(futureMessage, message);
            sent = output.send(message);
            if (sent) {
                log.info("Message {} sent", value("triggerId", futureMessage.getId()));
                runOnSuccessInterceptors(futureMessage, message);
            } else {
                runOnFailInterceptors(futureMessage, message);
            }
            return sent;
        } catch (MessageHandlingException e) {
            final JobExecutionException je = new JobExecutionException(e);
            je.setRefireImmediately(true);
            log.error("Message {} send error. Rescheduled!", value("triggerId", futureMessage.getId()));
            runOnFailInterceptors(futureMessage, message);
            throw je;
        } finally {
            runAfterSendInterceptors(futureMessage, message, sent);
        }
    }

    private Message<Map<String, Object>> buildMessage(FutureMessage futureMessage) {
        MessageBuilder<Map<String, Object>> messageBuilder = MessageBuilder.withPayload(futureMessage.getPayload())
                .setHeader("scheduleId", futureMessage.getId())
                .setHeader("destination", futureMessage.getDestination());
        runBuildMessageInterceptors(futureMessage, messageBuilder);
        return messageBuilder.build();
    }

    private void runBuildMessageInterceptors(FutureMessage futureMessage, MessageBuilder<Map<String, Object>> messageBuilder) {
        interceptorManager.buildOutputMessage(futureMessage, messageBuilder);
    }

    private void runBeforeSendInterceptors(FutureMessage futureMessage, Message message) throws JobExecutionException {
        try {
            interceptorManager.beforeSend(futureMessage, message);
        } catch (InterceptorException e) {
            throw new JobExecutionException(e);
        }
    }

    private void runAfterSendInterceptors(FutureMessage futureMessage, Message message, boolean sent) throws JobExecutionException {
        try {
            interceptorManager.afterSend(futureMessage, message, sent);
        } catch (InterceptorException e) {
            throw new JobExecutionException(e);
        }
    }

    private void runOnSuccessInterceptors(FutureMessage futureMessage, Message message) throws JobExecutionException {
        try {
            interceptorManager.onSuccess(futureMessage, message);
        } catch (InterceptorException e) {
            throw new JobExecutionException(e);
        }
    }

    private void runOnFailInterceptors(FutureMessage futureMessage, Message message) throws JobExecutionException {
        try {
            interceptorManager.onFail(futureMessage, message);
        } catch (InterceptorException e) {
            throw new JobExecutionException(e);
        }
    }
}
