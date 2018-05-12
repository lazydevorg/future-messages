package org.lazydev.futuremessages.schedule;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.lazydev.futuremessages.api.Message;
import org.mockito.ArgumentCaptor;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Date;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
public class MessageSchedulerTest {
    @MockBean
    private Scheduler scheduler;

    @Test
    public void createsJobOnInit() throws SchedulerException {
        final MessageScheduler messageScheduler = new MessageScheduler(scheduler);
        messageScheduler.init();
        ArgumentCaptor<JobDetail> captor = ArgumentCaptor.forClass(JobDetail.class);
        verify(scheduler).addJob(captor.capture(), eq(true));
        assertThat(captor.getValue())
                .extracting("key.name", "key.group", "jobClass.simpleName")
                .contains("messageSender", "DEFAULT", "MessageSenderJob");
    }

    @Test
    public void scheduleJob() throws SchedulerException {
        final MessageScheduler messageScheduler = new MessageScheduler(scheduler);
        final Instant now = Instant.now();
        ArgumentCaptor<Trigger> captor = ArgumentCaptor.forClass(Trigger.class);
        given(scheduler.scheduleJob(captor.capture())).willReturn(Date.from(now));

        final Message message = new Message();
        message.setStartAt(Instant.now());
        message.setPayloadData("field1", "value1");
        final ScheduledJob scheduledJob = messageScheduler.schedule(message);
        final Trigger trigger = captor.getValue();

        assertThat(scheduledJob)
                .extracting("startAt", "triggerId")
                .contains(now, trigger.getKey().getName());
    }
}