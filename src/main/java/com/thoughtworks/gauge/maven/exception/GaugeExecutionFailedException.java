package com.thoughtworks.gauge.maven.exception;

public class GaugeExecutionFailedException extends Exception {
    public GaugeExecutionFailedException(Throwable throwable) {
        super(throwable);
    }

    public GaugeExecutionFailedException() {

    }
}
