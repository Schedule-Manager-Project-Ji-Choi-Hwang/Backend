package backend.schedule.validation;

import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;


public class RequestDataValidation {

    public static void beanValidation(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            for (ObjectError error : bindingResult.getAllErrors()) {
                throw new IllegalArgumentException(error.getDefaultMessage());
            }
        }
    }
}
