package org.lazydev.futuremessages.interceptors;

public class InterceptorException extends Exception {
    public InterceptorException(String message, Throwable cause) {
        super(message, cause);
    }

    public InterceptorException(Throwable cause) {
        super(cause);
    }
}
