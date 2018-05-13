package org.lazydev.futuremessages.schedule;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandlingException;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MessageSenderRefireTest {
    @MockBean
    private MessageChannel output;

    @Autowired
    private MessageSender messageSender;

    @Test
    public void refireImmediatelyOnError() {
        final MessageHandlingException messageHandlingException = new MessageHandlingException(
                mock(GenericMessage.class), "Something went wrong!");

        given(output.send(any())).willThrow(messageHandlingException);

        assertThatExceptionOfType(JobExecutionException.class)
                .isThrownBy(() -> messageSender.send("myid", Collections.singletonMap("field1", "value1")))
                .satisfies(e -> assertThat(e.refireImmediately())
                        .describedAs("Job set for immediate refire")
                        .isTrue());
    }
}