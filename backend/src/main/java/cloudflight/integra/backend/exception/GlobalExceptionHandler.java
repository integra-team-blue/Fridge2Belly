package cloudflight.integra.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> fieldErrors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest()
                .body(Map.of(
                             "status",
                             400,
                             "error",
                             "Bad Request",
                             "message",
                             "Validation failed",
                             "fieldErrors",
                             fieldErrors
                ));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                             "status",
                             404,
                             "error",
                             "Not Found",
                             "message",
                             ex.getMessage()
                ));
    }

    @ExceptionHandler(MealNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleMealNotFound(MealNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                             "status",
                             404,
                             "error",
                             "Not Found",
                             "message",
                             ex.getMessage()
                ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body(Map.of(
                             "status",
                             400,
                             "error",
                             "Bad Request",
                             "message",
                             ex.getMessage()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                             "status",
                             500,
                             "error",
                             "Internal Server Error",
                             "message",
                             "Something went wrong"
                ));
    }
}
