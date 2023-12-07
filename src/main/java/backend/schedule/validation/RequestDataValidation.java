package backend.schedule.validation;

import backend.schedule.dto.MessageReturnDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.stream.Collectors;

public class RequestDataValidation {

    public static List<String> beanValidation(BindingResult bindingResult) {

        return bindingResult.getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.toList());
    }
}
