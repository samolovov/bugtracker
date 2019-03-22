package samolovov.bugtracker.enums;

import lombok.Getter;

@Getter
public enum ServerMessage {
    PROJECT_NOT_FOUND("Project not found"),
    TASK_NOT_FOUND("Task not found"),
    TASK_UPDATE_FORBIDDEN("Task closed and cannot be updated");

    private final String text;

    ServerMessage(String text) {
        this.text = text;
    }
}
