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
        MessageMetadata metadata = buildMetadata(context);
        messageSender.send(futureMessage, metadata);
    }

    private FutureMessage buildFutureMessage(JobExecutionContext context) throws JobExecutionException {
        final String id = getTriggerId(context);
        JobDataMap jobData = context.getMergedJobDataMap();
        String destination = getDestination(jobData);
        Map<String, Object> payload = getPayload(jobData);
        return new FutureMessage(id, destination, payload);
    }

    private MessageMetadata buildMetadata(JobExecutionContext context) throws JobExecutionException {
        return getMetadata(context.getMergedJobDataMap());
    }

    private String getDestination(JobDataMap jobData) {
        return jobData.getString("destination");
    }

    private String getTriggerId(JobExecutionContext context) {
        return context.getTrigger().getKey().getName();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getPayload(JobDataMap jobData) throws JobExecutionException {
        return parseJson(jobData, "payload", Map.class);
    }

    private MessageMetadata getMetadata(JobDataMap jobData) throws JobExecutionException {
        return parseJson(jobData, "metadata", MessageMetadata.class);
    }

    private <T> T parseJson(JobDataMap jobData, String key, Class<T> clazz) throws JobExecutionException {
        try {
            return objectMapper.readValue(jobData.getString(key), clazz);
        } catch (IOException e) {
            throw new JobExecutionException("The job metadata is not a valid json", e);
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
