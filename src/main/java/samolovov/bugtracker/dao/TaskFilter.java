package samolovov.bugtracker.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import samolovov.bugtracker.enums.TaskStatus;

import java.util.Date;

@AllArgsConstructor
@Getter
public class TaskFilter {
    private Date createdStart;
    private Date createdEnd;
    private Date modifiedStart;
    private Date modifiedEnd;
    private TaskStatus status;
    private Integer priority;
}
