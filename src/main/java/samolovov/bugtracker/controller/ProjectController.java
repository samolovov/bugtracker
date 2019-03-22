package samolovov.bugtracker.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import samolovov.bugtracker.dao.ProjectBean;
import samolovov.bugtracker.dao.TaskBean;
import samolovov.bugtracker.dao.TaskFilter;
import samolovov.bugtracker.enums.TaskStatus;
import samolovov.bugtracker.service.ProjectService;
import samolovov.bugtracker.service.TaskService;

import javax.validation.Valid;
import java.util.Date;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects")
public class ProjectController {
    private final ProjectService projectService;
    private final TaskService taskService;

    @GetMapping("/-/tasks")
    public Page<TaskBean> listTasks(
            @RequestParam(required = false) @DateTimeFormat(pattern="dd-MM-yyyy") Date createdStart,
            @RequestParam(required = false) @DateTimeFormat(pattern="dd-MM-yyyy") Date createdEnd,
            @RequestParam(required = false) @DateTimeFormat(pattern="dd-MM-yyyy") Date modifiedStart,
            @RequestParam(required = false) @DateTimeFormat(pattern="dd-MM-yyyy") Date modifiedEnd,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) Integer priority,
            Pageable pageable) {
        TaskFilter taskFilter = new TaskFilter(createdStart, createdEnd, modifiedStart, modifiedEnd, status, priority);
        return taskService.list(taskFilter, pageable);
    }

    @GetMapping
    public Page<ProjectBean> list(Pageable pageable) {
        return projectService.list(pageable);
    }

    @GetMapping("/{id}")
    public ProjectBean get(@PathVariable int id) {
        return projectService.get(id);
    }

    @PostMapping
    public ProjectBean create(@Valid @RequestBody ProjectBean projectBean) {
        return projectService.create(projectBean);
    }

    @PutMapping("/{id}")
    public ProjectBean update(@PathVariable int id, @Valid @RequestBody ProjectBean projectBean) {
        projectBean.setId(id);
        return projectService.update(projectBean);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        projectService.delete(id);
    }
}
