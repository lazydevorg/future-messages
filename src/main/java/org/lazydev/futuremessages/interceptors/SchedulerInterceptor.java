package org.lazydev.futuremessages.interceptors;

import org.lazydev.futuremessages.api.Message;
import org.lazydev.futuremessages.schedule.FutureMessage;
import org.quartz.JobExecutionContext;
import org.quartz.TriggerBuilder;
import org.springframework.messaging.support.MessageBuilder;

public interface SchedulerInterceptor {
    default void buildTrigger(TriggerBuilder triggerBuilder, Message message) throws InterceptorException {
    }

    default void buildFutureMessage(FutureMessage futureMessage, JobExecutionContext context) throws InterceptorException {
    }

    default void buildOutputMessage(FutureMessage futureMessage, MessageBuilder messageBuilder) {
    }

    default void beforeSend(FutureMessage futureMessage, org.springframework.messaging.Message message) throws InterceptorException {
    }

    default void onSuccess(FutureMessage futureMessage, org.springframework.messaging.Message message) throws InterceptorException {
    }

    default void onFail(FutureMessage futureMessage, org.springframework.messaging.Message message) throws InterceptorException {
    }

    default void afterSend(FutureMessage futureMessage, org.springframework.messaging.Message message, boolean sent) throws InterceptorException {
    }
}
