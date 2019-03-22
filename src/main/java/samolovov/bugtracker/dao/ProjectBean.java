package samolovov.bugtracker.dao;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class ProjectBean extends PersistentBean {
    @NotBlank(message = "Project title cannot be empty")
    private String title;
    private String description;
}
