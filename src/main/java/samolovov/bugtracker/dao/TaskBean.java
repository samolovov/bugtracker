package samolovov.bugtracker.dao;

import lombok.Getter;
import lombok.Setter;
import samolovov.bugtracker.enums.TaskStatus;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class TaskBean extends PersistentBean {
    private Integer projectId;
    @NotBlank(message = "Task title cannot be empty")
    private String title;
    private String description;
    @Min(value = 0, message = "Priority must be positive integer")
    private int priority;
    @NotNull(message = "Status cannot be null")
    private TaskStatus status;
}
