package org.lazydev.futuremessages.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lazydev.futuremessages.schedule.MessageScheduler;
import org.lazydev.futuremessages.schedule.ScheduledJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ScheduleController.class)
public class ScheduleControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private MessageScheduler scheduler;

    @Autowired
    private ObjectMapper mapper;

    private String buildMessageJson(Instant startAt) throws JsonProcessingException {
        Message message = new Message();
        message.setStartAt(startAt);
        message.setPayload("example payload");
        return mapper.writeValueAsString(message);
    }

    @Test
    public void scheduleJob() throws Exception {
        Instant startAt = Instant.now().plusSeconds(60);
        ScheduledJob job = new ScheduledJob(startAt, UUID.randomUUID().toString());
        given(scheduler.schedule(any(Message.class))).willReturn(job);

        mvc.perform(
                post("/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildMessageJson(startAt)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.startAt", is(startAt.toString())));
    }

    @Test
    public void rejectMessageWithDateInThePast() throws Exception {
        Instant startAt = Instant.now().minusSeconds(60);

        mvc.perform(
                post("/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildMessageJson(startAt)))
                .andExpect(status().isBadRequest());
    }
}