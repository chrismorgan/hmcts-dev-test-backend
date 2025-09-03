package uk.gov.hmcts.reform.dev.validators;

import java.time.LocalDateTime;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import uk.gov.hmcts.reform.dev.entity.Task;

@Component
public class TaskValidator implements Validator {

    /**
     * This Validator validates only Date instances
     */
    public boolean supports(Class clazz) {
        return Task.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors e) {
        ValidationUtils.rejectIfEmpty(e, "dueDate", "due-date",
                                      "due date must be supplied");
        ValidationUtils.rejectIfEmpty(e, "title", "title",
                                      "title must be supplied");
        ValidationUtils.rejectIfEmpty(e, "status", "status",
                                      "status must be supplied");

        Task task = (Task) obj;
        if (LocalDateTime.now().isAfter(task.getDueDate())) {
            e.rejectValue("dueDate", "due-date",
                          "The due date should be in the future");
        }
    }

}
