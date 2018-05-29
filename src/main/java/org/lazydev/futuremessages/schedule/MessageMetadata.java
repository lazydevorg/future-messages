package org.lazydev.futuremessages.schedule;

public class MessageMetadata {
    private long traceId;
    private boolean sampled;

    public long getTraceId() {
        return traceId;
    }

    public void setTraceId(long traceId) {
        this.traceId = traceId;
    }

    public boolean getSampled() {
        return sampled;
    }

    public void setSampled(boolean sampled) {
        this.sampled = sampled;
    }
}
