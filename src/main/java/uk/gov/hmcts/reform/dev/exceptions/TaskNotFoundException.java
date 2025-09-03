package uk.gov.hmcts.reform.dev.exceptions;

import lombok.Getter;

public class TaskNotFoundException extends RuntimeException {

    @Getter
    private final String code;

    public TaskNotFoundException(String code, String message) {
        super(message);
        this.code = code;
    }

    public TaskNotFoundException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}
