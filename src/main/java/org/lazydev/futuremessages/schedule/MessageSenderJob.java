package org.lazydev.futuremessages.schedule;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Map;

@SuppressWarnings("SpringJavaAutowiredMembersInspection")
class MessageSenderJob implements Job {
    private MessageSender messageSender;
    private ObjectMapper objectMapper;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        FutureMessage futureMessage = buildFutureMessage(context);
        messageSender.send(futureMessage);
    }

    private FutureMessage buildFutureMessage(JobExecutionContext context) throws JobExecutionException {
        final String id = getTriggerId(context);
        JobDataMap jobData = context.getMergedJobDataMap();
        String destination = getDestination(jobData);
        Map<String, Object> payload = getPayload(jobData);
        return new FutureMessage(id, destination, payload);
    }

    private String getDestination(JobDataMap jobData) {
        return jobData.getString("destination");
    }

    private String getTriggerId(JobExecutionContext context) {
        return context.getTrigger().getKey().getName();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getPayload(JobDataMap jobData) throws JobExecutionException {
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
