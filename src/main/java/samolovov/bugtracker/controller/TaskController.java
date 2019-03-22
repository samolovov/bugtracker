package samolovov.bugtracker.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import samolovov.bugtracker.dao.TaskBean;
import samolovov.bugtracker.service.TaskService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects/{projectId}/tasks")
public class TaskController {
    private final TaskService taskService;

    @GetMapping
    public Page<TaskBean> list(@PathVariable int projectId, Pageable pageable) {
        return taskService.list(projectId, pageable);
    }

    @GetMapping("/{id}")
    public TaskBean get(@PathVariable int projectId, @PathVariable int id) {
        return taskService.get(projectId, id);
    }

    @PostMapping
    public TaskBean create(@PathVariable int projectId, @Valid @RequestBody TaskBean taskBean) {
        taskBean.setProjectId(projectId);
        return taskService.create(taskBean);
    }

    @PutMapping("/{id}")
    public TaskBean update(@PathVariable int projectId, @PathVariable int id, @Valid @RequestBody TaskBean taskBean) {
        taskBean.setId(id);
        taskBean.setProjectId(projectId);
        return taskService.update(taskBean);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int projectId, @PathVariable int id) {
        taskService.delete(projectId, id);
    }
}
