package com.kin.ecosystem.exception;

public class TaskFailedException extends Exception {

    public TaskFailedException(Throwable cause) {
        super(cause);
    }

    public TaskFailedException(String message) {
        super(message);
    }
}
