package samolovov.bugtracker.util;

import org.springframework.http.HttpStatus;
import samolovov.bugtracker.enums.ServerMessage;
import samolovov.bugtracker.exception.ServerException;

public class Preconditions {
    public static void checkNotNull(Object object, ServerMessage message, HttpStatus httpStatus) {
        if (object == null) {
            throw new ServerException(message, httpStatus);
        }
    }

    public static void checkFalse(boolean condition, ServerMessage message, HttpStatus httpStatus) {
        if (condition) {
            throw new ServerException(message, httpStatus);
        }
    }
}
