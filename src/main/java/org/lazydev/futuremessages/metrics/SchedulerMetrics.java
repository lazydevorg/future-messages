package org.lazydev.futuremessages.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.listeners.SchedulerListenerSupport;
import org.quartz.listeners.TriggerListenerSupport;
import org.springframework.stereotype.Component;

@Component
public class SchedulerMetrics implements MeterBinder {
    private final Scheduler scheduler;
    private final MeterRegistry registry;
    private Counter jobScheduledCounter;
    private Counter firedTriggersCounter;
    private Counter misfiredTriggerCounter;

    public SchedulerMetrics(Scheduler scheduler, MeterRegistry registry) {
        this.scheduler = scheduler;
        this.registry = registry;
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        jobScheduledCounter = Counter
                .builder("scheduler.scheduled")
                .description("Scheduled jobs")
                .register(registry);

        firedTriggersCounter = Counter
                .builder("scheduler.triggers")
                .description("Trigger fired")
                .tag("event", "fired")
                .register(registry);

        misfiredTriggerCounter = Counter
                .builder("scheduler.triggers")
                .tag("event", "misfired")
                .register(registry);

        try {
            scheduler.getListenerManager().addSchedulerListener(new JobListener(jobScheduledCounter));
            scheduler.getListenerManager().addTriggerListener(new TriggerListener(firedTriggersCounter, misfiredTriggerCounter));
        } catch (SchedulerException e) {
            throw new IllegalStateException(e);
        }
    }

    class JobListener extends SchedulerListenerSupport {
        private final Counter jobScheduledCounter;

        JobListener(Counter jobScheduledCounter) {
            this.jobScheduledCounter = jobScheduledCounter;
        }

        @Override
        public void jobScheduled(Trigger trigger) {
            jobScheduledCounter.increment();
        }
    }

    class TriggerListener extends TriggerListenerSupport {
        private final Counter firedTriggersCounter;
        private final Counter misfiredTriggerCounter;

        TriggerListener(Counter firedTriggersCounter, Counter misfiredTriggerCounter) {
            this.firedTriggersCounter = firedTriggersCounter;
            this.misfiredTriggerCounter = misfiredTriggerCounter;
        }

        @Override
        public String getName() {
            return "SchedulerMetrics";
        }

        @Override
        public void triggerFired(Trigger trigger, JobExecutionContext context) {
            firedTriggersCounter.increment();
        }

        @Override
        public void triggerMisfired(Trigger trigger) {
            misfiredTriggerCounter.increment();
        }
    }
}
