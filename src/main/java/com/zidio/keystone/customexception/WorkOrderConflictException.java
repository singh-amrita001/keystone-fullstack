package com.zidio.keystone.customexception;

public class WorkOrderConflictException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public WorkOrderConflictException(String message) {
        super(message);
    }
}