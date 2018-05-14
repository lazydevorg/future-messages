package org.lazydev.futuremessages.schedule;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Map;

class MessageSenderJob implements Job {
    private static final Logger log = LoggerFactory.getLogger(MessageSenderJob.class);
    private MessageSender messageSender;
    private ObjectMapper objectMapper;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        final String id = getTriggerId(context);
        JobDataMap jobData = context.getMergedJobDataMap();
        String destination = getDestination(jobData);
        Map payload = getPayload(jobData);
        FutureMessage futureMessage = new FutureMessage(id, destination, payload);
        messageSender.send(futureMessage);
    }

    private String getDestination(JobDataMap jobData) {
        return jobData.getString("destination");
    }

    private String getTriggerId(JobExecutionContext context) {
        return context.getTrigger().getKey().getName();
    }

    private Map getPayload(JobDataMap jobData) throws JobExecutionException {
        try {
            return objectMapper.readValue(jobData.getString("payload"), Map.class);
        } catch (IOException e) {
            throw new JobExecutionException("The job payload is not a valid json", e);
        }
    }

    @Autowired
    public void setMessageSender(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}
