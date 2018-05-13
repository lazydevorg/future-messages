package org.lazydev.futuremessages.schedule;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MessageSenderTest {
    @Autowired
    private MessageCollector collector;

    @Autowired
    private MessageChannel output;

    @Autowired
    private MessageSender messageSender;

    @Test
    public void send() throws JobExecutionException {
        messageSender.send("myid", Collections.singletonMap("field1", "value1"));
        final Message<String> message = (Message<String>) collector.forChannel(output).poll();

        assertThat(message.getPayload()).isEqualTo("{\"field1\":\"value1\"}");
        assertThat(message.getHeaders())
                .containsEntry("scheduleId", "myid");
    }
}