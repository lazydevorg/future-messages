package org.lazydev.futuremessages.tracing;

import brave.Tracer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.lazydev.futuremessages.api.Message;
import org.lazydev.futuremessages.interceptors.InterceptorException;
import org.lazydev.futuremessages.interceptors.SchedulerInterceptor;
import org.lazydev.futuremessages.schedule.FutureMessage;
import org.quartz.JobExecutionContext;
import org.quartz.TriggerBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TracingInterceptor implements SchedulerInterceptor {
    private static final String TRACING_INTERCEPTOR_DATA_KEY = "tracingInterceptorData";
    private final TracingHelper tracingHelper;
    private final ObjectMapper objectMapper;

    public TracingInterceptor(TracingHelper tracingHelper, ObjectMapper objectMapper) {
        this.tracingHelper = tracingHelper;
        this.objectMapper = objectMapper;
    }

    @Override
    public void buildTrigger(TriggerBuilder triggerBuilder, Message message) throws InterceptorException {
        try {
            triggerBuilder.usingJobData(TRACING_INTERCEPTOR_DATA_KEY, objectMapper.writeValueAsString(buildTracingData()));
        } catch (JsonProcessingException e) {
            throw new InterceptorException(e);
        }
    }

    @Override
    public void buildFutureMessage(FutureMessage futureMessage, JobExecutionContext context) throws InterceptorException {
        try {
            String tracingJsonData = context.getMergedJobDataMap().getString(TRACING_INTERCEPTOR_DATA_KEY);
            TracingData tracingData = objectMapper.readValue(tracingJsonData, TracingData.class);
            futureMessage.putMetadata(TRACING_INTERCEPTOR_DATA_KEY, tracingData);
        } catch (IOException e) {
            throw new InterceptorException(e);
        }
    }

    @Override
    public void beforeSend(FutureMessage futureMessage, org.springframework.messaging.Message message) {
        TracingData tracingData = futureMessage.getMetadata(TRACING_INTERCEPTOR_DATA_KEY, TracingData.class);
        Tracer.SpanInScope spanInScope = tracingHelper.joinTrace(tracingData.getTraceId(), tracingData.getParentId(), tracingData.isSampled());
        tracingData.setSpanInScope(spanInScope);
    }

    @Override
    public void afterSend(FutureMessage futureMessage, org.springframework.messaging.Message message, boolean sent) {
        TracingData tracingData = futureMessage.getMetadata(TRACING_INTERCEPTOR_DATA_KEY, TracingData.class);
        tracingData.getSpanInScope().close();
    }

    private TracingData buildTracingData() {
        TracingData tracingData = new TracingData();
        tracingData.setTraceId(tracingHelper.traceId());
        tracingData.setParentId(tracingHelper.parentId());
        tracingData.setSampled(tracingHelper.sampled());
        return tracingData;
    }

    static class TracingData {
        private long traceId;
        private Long parentId;
        private boolean sampled;
        private Tracer.SpanInScope spanInScope;

        public long getTraceId() {
            return traceId;
        }

        public void setTraceId(long traceId) {
            this.traceId = traceId;
        }

        public Long getParentId() {
            return parentId;
        }

        public void setParentId(Long parentId) {
            this.parentId = parentId;
        }

        public boolean isSampled() {
            return sampled;
        }

        public void setSampled(boolean sampled) {
            this.sampled = sampled;
        }

        public Tracer.SpanInScope getSpanInScope() {
            return spanInScope;
        }

        public void setSpanInScope(Tracer.SpanInScope spanInScope) {
            this.spanInScope = spanInScope;
        }
    }
}
