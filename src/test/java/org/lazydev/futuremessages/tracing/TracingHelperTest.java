package org.lazydev.futuremessages.tracing;

import brave.Span;
import brave.Tracer;
import brave.propagation.TraceContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TracingHelperTest {
    @Autowired
    private Tracer tracer;

    @Autowired
    private TracingHelper tracingHelper;

    @Test
    public void testTracingData() {
        Span span = tracer.nextSpan();
        tracer.withSpanInScope(span);
        assertThat(tracingHelper.traceId()).isEqualTo(span.context().traceId());
        assertThat(tracingHelper.parentId()).isEqualTo(span.context().parentId());
        assertThat(tracingHelper.sampled()).isEqualTo(span.context().sampled());
    }

    @Test
    public void joinTrace() {
        tracingHelper.joinTrace(1234L, Long.valueOf(4321L), true);
        TraceContext context = tracer.currentSpan().context();
        assertThat(context.traceId()).isEqualTo(1234L);
        assertThat(context.parentId()).isEqualTo(4321L);
        assertThat(context.sampled()).isEqualTo(true);
    }
}