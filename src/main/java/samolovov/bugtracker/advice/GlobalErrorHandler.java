package samolovov.bugtracker.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import samolovov.bugtracker.exception.ServerException;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleValidationException(MethodArgumentNotValidException ex) {
        List<String> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body("There are following errors: " + errors);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, HttpMessageNotReadableException.class})
    public ResponseEntity handleArgumentMismatchExeption(RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(ServerException.class)
    public ResponseEntity handleServerException(ServerException e) {
        return ResponseEntity.status(e.getStatus()).body(e.getMessage());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity handlePageNotFoundException() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resource not found");
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity handleAll(Throwable e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unknown server error");
    }
}
