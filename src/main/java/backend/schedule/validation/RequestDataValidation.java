package backend.schedule.validation;

import backend.schedule.dto.MessageReturnDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RequestDataValidation {

    public static void beanValidation(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            for (ObjectError error : bindingResult.getAllErrors()) {
                throw new IllegalArgumentException(error.getDefaultMessage());
            }
        }
    }
}
