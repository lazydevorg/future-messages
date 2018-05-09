package org.lazydev.futuremessages.schedule;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageSenderJob implements Job {
    private static final Logger log = LoggerFactory.getLogger(MessageSenderJob.class);

    @Override
    public void execute(JobExecutionContext context) {
        log.debug(context.toString());
    }
}
