package org.lazydev.futuremessages.schedule;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

class MessageSenderJob implements Job {
    private static final Logger log = LoggerFactory.getLogger(MessageSenderJob.class);
    private MessageSender messageSender;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Map<String, Object> payload = context.getMergedJobDataMap().getWrappedMap();
        final String id = context.getTrigger().getKey().getName();
        messageSender.send(id, payload);
    }

    @Autowired
    public void setMessageSender(MessageSender messageSender) {
        this.messageSender = messageSender;
    }
}
