package uk.gov.hmcts.reform.dev.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import uk.gov.hmcts.reform.dev.model.ErrorModel;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({TaskNotFoundException.class})
    public ResponseEntity<Object> handleTaskNotFoundException(TaskNotFoundException exception) {
        return new ResponseEntity<>(createErrorModel(exception.getCode(), HttpStatus.NOT_FOUND, exception.getMessage()),
        HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({BusinessRuleException.class})
    public ResponseEntity<Object> handleBusinessRuleException(BusinessRuleException exception) {
        return new ResponseEntity<>(
            createErrorModel(
                exception.getCode(),
                HttpStatus.UNPROCESSABLE_ENTITY,
                exception.getMessage()
            ),
            HttpStatus.UNPROCESSABLE_ENTITY
        );
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseEntity<Object> handleMessageNotReadableException(HttpMessageNotReadableException exception) {
        return new ResponseEntity<>(createErrorModel("task", HttpStatus.NOT_FOUND, exception.getMostSpecificCause().getMessage()),
                                    HttpStatus.NOT_FOUND);
    }

    private ErrorModel createErrorModel(String code, HttpStatus httpStatus, String message) {
        return new ErrorModel()
            .status(httpStatus.value())
            .code(code)
            .message(message);
    }
}
