package uk.gov.hmcts.reform.dev.exceptions;

import lombok.Getter;

public class BusinessRuleException extends RuntimeException {

    @Getter
    private final String code;

    public BusinessRuleException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public BusinessRuleException(String code, String message) {
        super(message);
        this.code = code;
    }
}
