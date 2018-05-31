package org.lazydev.futuremessages.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.lazydev.futuremessages.interceptors.InterceptorException;
import org.lazydev.futuremessages.schedule.MessageScheduler;
import org.lazydev.futuremessages.schedule.ScheduledJob;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class ScheduleController {
    private final MessageScheduler scheduler;

    @Autowired
    public ScheduleController(MessageScheduler scheduler) {
        this.scheduler = scheduler;
    }

    @PostMapping(value = "/schedule")
    public ResponseEntity<ScheduleResponse> schedule(@RequestBody @Valid Message message) throws SchedulerException, JsonProcessingException, InterceptorException {
        ScheduledJob job = scheduler.schedule(message);
        ScheduleResponse response = new ScheduleResponse(job);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
