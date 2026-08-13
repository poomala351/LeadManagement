package com.example.LeadManagement.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j

public class AlreadyExistsException extends RuntimeException {

    public AlreadyExistsException(String message) {
        super(message);
    }
}
