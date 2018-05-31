package org.lazydev.futuremessages.interceptors;

import org.lazydev.futuremessages.api.Message;
import org.lazydev.futuremessages.schedule.FutureMessage;
import org.lazydev.futuremessages.tracing.TracingInterceptor;
import org.quartz.JobExecutionContext;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SchedulerInterceptorManager implements SchedulerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(SchedulerInterceptorManager.class);

    private static final List<Class<? extends SchedulerInterceptor>> interceptorsClasses = new ArrayList<>() {{
        add(TracingInterceptor.class);
    }};
    private final ApplicationContext context;
    private List<? extends SchedulerInterceptor> interceptors;

    public SchedulerInterceptorManager(ApplicationContext context) {
        this.context = context;
    }

    @PostConstruct
    public void init() {
        loadInterceptors();
    }

    private void loadInterceptors() {
        interceptors = interceptorsClasses.stream()
                .map(context::getBean)
                .collect(Collectors.toList());
        logInterceptors();
    }

    private void logInterceptors() {
        log.info("Scheduler interceptors loaded: {}", interceptors.stream().map(SchedulerInterceptor::toString).collect(Collectors.joining(",")));
    }

    @Override
    public void buildTrigger(TriggerBuilder triggerBuilder, Message message) throws InterceptorException {
        for (SchedulerInterceptor schedulerInterceptor : interceptors) {
            schedulerInterceptor.buildTrigger(triggerBuilder, message);
        }
    }

    @Override
    public void buildFutureMessage(FutureMessage futureMessage, JobExecutionContext context) throws InterceptorException {
        for (SchedulerInterceptor schedulerInterceptor : interceptors) {
            schedulerInterceptor.buildFutureMessage(futureMessage, context);
        }
    }

    @Override
    public void beforeSend(FutureMessage futureMessage, org.springframework.messaging.Message message) throws InterceptorException {
        for (SchedulerInterceptor schedulerInterceptor : interceptors) {
            schedulerInterceptor.beforeSend(futureMessage, message);
        }
    }

    @Override
    public void onSuccess(FutureMessage futureMessage, org.springframework.messaging.Message message) throws InterceptorException {
        for (SchedulerInterceptor schedulerInterceptor : interceptors) {
            schedulerInterceptor.onSuccess(futureMessage, message);
        }
    }

    @Override
    public void onFail(FutureMessage futureMessage, org.springframework.messaging.Message message) throws InterceptorException {
        for (SchedulerInterceptor schedulerInterceptor : interceptors) {
            schedulerInterceptor.onFail(futureMessage, message);
        }
    }

    @Override
    public void afterSend(FutureMessage futureMessage, org.springframework.messaging.Message message, boolean sent) throws InterceptorException {
        for (SchedulerInterceptor schedulerInterceptor : interceptors) {
            schedulerInterceptor.afterSend(futureMessage, message, sent);
        }
    }
}
