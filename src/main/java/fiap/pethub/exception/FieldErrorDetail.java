package fiap.pethub.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FieldErrorDetail {
    private String field;
    private String message;
}

