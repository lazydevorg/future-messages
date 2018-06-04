package org.lazydev.futuremessages.tracing;

import brave.Span;
import brave.Tracer;
import brave.propagation.TraceContext;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class TracingHelper {
    private final brave.Tracer tracer;
    private final Random random = new Random(System.currentTimeMillis());

    public TracingHelper(brave.Tracer tracer) {
        this.tracer = tracer;
    }

    public long traceId() {
        return context().traceId();
    }

    public Long parentId() {
        return context().parentId();
    }

    public boolean sampled() {
        return context().sampled();
    }

    public Tracer.SpanInScope joinTrace(Long traceId, Long parentId, boolean sampled) {
        return tracer.withSpanInScope(buildSpan(traceId, parentId, sampled));
    }

    private Span currentSpan() {
        return tracer.currentSpan();
    }

    private TraceContext context() {
        return currentSpan().context();
    }

    private Span buildSpan(Long traceId, Long parentId, boolean sampled) {
        return tracer.toSpan(buildTraceContext(traceId, parentId, sampled));
    }

    private TraceContext buildTraceContext(Long traceId, Long parentId, boolean sampled) {
        return TraceContext.newBuilder()
                .traceId(traceId)
                .parentId(parentId)
                .spanId(random.nextLong())
                .sampled(sampled)
                .build();
    }
}
