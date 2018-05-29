package org.lazydev.futuremessages.schedule;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lazydev.futuremessages.api.Message;
import org.lazydev.futuremessages.tracing.TracingHelper;
import org.mockito.ArgumentCaptor;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
public class MessageSchedulerTest {
    @MockBean
    private Scheduler scheduler;

    @MockBean
    private TracingHelper tracingHelper;

    private ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();

    @Before
    public void mockTracer() {
        given(tracingHelper.traceId()).willReturn(1234L);
    }

    @Test
    public void createsJobOnInit() throws SchedulerException {
        final MessageScheduler messageScheduler = new MessageScheduler(scheduler, objectMapper, tracingHelper);
        messageScheduler.init();
        ArgumentCaptor<JobDetail> captor = ArgumentCaptor.forClass(JobDetail.class);
        verify(scheduler).addJob(captor.capture(), eq(true));
        assertThat(captor.getValue())
                .extracting("key.name", "key.group", "jobClass.simpleName")
                .contains("MessageSender", "Futures", "MessageSenderJob");
    }

    @Test
    public void scheduleJob() throws SchedulerException, JsonProcessingException {
        final MessageScheduler messageScheduler = new MessageScheduler(scheduler, objectMapper, tracingHelper);
        final Date now = Date.from(Instant.now());
        ArgumentCaptor<Trigger> captor = ArgumentCaptor.forClass(Trigger.class);
        given(scheduler.scheduleJob(captor.capture())).willReturn(now);

        final Message message = new Message();
        message.setStart(Instant.now());
        message.setDestination("destination");
        message.setPayloadData("field1", "value1");
        final ScheduledJob scheduledJob = messageScheduler.schedule(message);
        final Trigger trigger = captor.getValue();

        assertThat(scheduledJob)
                .extracting("start", "triggerId")
                .contains(now.toInstant(), trigger.getKey().getName());
    }
}