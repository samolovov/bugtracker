package samolovov.bugtracker.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import samolovov.bugtracker.enums.ServerMessage;

@Getter
public class ServerException extends RuntimeException {
    private final String message;
    private final HttpStatus status;

    public ServerException(ServerMessage serverMessage, HttpStatus status) {
        this.message = serverMessage.getText();
        this.status = status;
    }

}
