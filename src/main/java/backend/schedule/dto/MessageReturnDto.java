package backend.schedule.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageReturnDto<T> {

    private int status;

    private String result;

    private T errorMessage;

    private T message;

    public MessageReturnDto<?> badRequestFail(T errorMessage) {
        return new MessageReturnDto(HttpStatus.BAD_REQUEST.value(), "Fail", errorMessage, null);
    }

    public MessageReturnDto<?> okSuccess(T message) {
        return new MessageReturnDto(HttpStatus.OK.value(), "Success", null, message);
    }



}
