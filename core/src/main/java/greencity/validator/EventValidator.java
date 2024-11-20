package greencity.validator;

import greencity.annotations.EventValidation;
import greencity.dto.event.EventDetailsUpdate;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EventValidator implements ConstraintValidator<EventValidation, EventDetailsUpdate> {

    private int titleMaxSize;
    private EventDaysValidator eventDaysValidator;


    @Override
    public void initialize(EventValidation constraintAnnotation) {
        titleMaxSize = constraintAnnotation.titleMaxSize();
        eventDaysValidator=new EventDaysValidator();
    }

    @Override
    public boolean isValid(EventDetailsUpdate value, ConstraintValidatorContext context) throws RuntimeException {
        var title = value.getTitle();
        if (title.isEmpty() || title.length() > titleMaxSize) return false;
        if(!eventDaysValidator.checkEventDays(value.getEventDays())) return false;
        return true;
    }

}
