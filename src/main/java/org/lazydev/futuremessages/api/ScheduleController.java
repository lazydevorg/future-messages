package org.lazydev.futuremessages.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.lazydev.futuremessages.schedule.MessageScheduler;
import org.lazydev.futuremessages.schedule.ScheduledJob;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class ScheduleController {
    private static final Logger log = LoggerFactory.getLogger(ScheduleController.class);
    private final MessageScheduler scheduler;

    @Autowired
    public ScheduleController(MessageScheduler scheduler) {
        this.scheduler = scheduler;
    }

    @PostMapping(value = "/schedule", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ScheduleResponse> schedule(@RequestBody @Valid Message message) throws SchedulerException, JsonProcessingException {
        ScheduledJob job = scheduler.schedule(message);
        ScheduleResponse response = new ScheduleResponse(job);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
