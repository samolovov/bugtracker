package samolovov.bugtracker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import samolovov.bugtracker.dao.TaskBean;
import samolovov.bugtracker.dao.TaskFilter;
import samolovov.bugtracker.entity.Project;
import samolovov.bugtracker.entity.Task;
import samolovov.bugtracker.enums.TaskStatus;
import samolovov.bugtracker.repository.ProjectRepository;
import samolovov.bugtracker.repository.TaskRepository;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

import static samolovov.bugtracker.enums.ServerMessage.PROJECT_NOT_FOUND;
import static samolovov.bugtracker.enums.ServerMessage.TASK_NOT_FOUND;
import static samolovov.bugtracker.enums.ServerMessage.TASK_UPDATE_FORBIDDEN;
import static samolovov.bugtracker.util.Preconditions.checkFalse;
import static samolovov.bugtracker.util.Preconditions.checkNotNull;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskService extends PersistentService<TaskBean, Task> {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    @Transactional(readOnly = true)
    public TaskBean get(int projectId, int id) {
        Task task = taskRepository.findByIdAndProjectId(id, projectId);
        checkNotNull(task, TASK_NOT_FOUND, HttpStatus.NOT_FOUND);

        return toBean(task);
    }

    @Override
    public TaskBean create(TaskBean bean) {
        Project project = projectRepository.findOneById(bean.getProjectId());
        checkNotNull(project, PROJECT_NOT_FOUND, HttpStatus.NOT_FOUND);

        Task task = new Task();
        task.setProject(project);
        beanToEntity(bean, task);
        return toBean(taskRepository.save(task));
    }

    @Override
    public TaskBean update(TaskBean bean) {
        Project project = projectRepository.findOneById(bean.getProjectId());
        checkNotNull(project, PROJECT_NOT_FOUND, HttpStatus.NOT_FOUND);

        Task task = taskRepository.findOneById(bean.getId());
        checkNotNull(task, TASK_NOT_FOUND, HttpStatus.NOT_FOUND);
        checkFalse(task.getStatus() == TaskStatus.CLOSED, TASK_UPDATE_FORBIDDEN, HttpStatus.FORBIDDEN);

        task.setProject(project);
        beanToEntity(bean, task);
        return toBean(taskRepository.save(task));
    }

    @Transactional(readOnly = true)
    public Page<TaskBean> list(int projectId, Pageable pageable) {
        Project project = projectRepository.findOneById(projectId);
        checkNotNull(project, PROJECT_NOT_FOUND, HttpStatus.NOT_FOUND);

        Page<Task> tasks = taskRepository.findByProject(project, pageable);
        return toBeanPage(tasks);
    }

    public void delete(int projectId, int id) {
        Task task = taskRepository.findByIdAndProjectId(id, projectId);
        checkNotNull(task, TASK_NOT_FOUND, HttpStatus.NOT_FOUND);

        task.setDeleted(true);
        taskRepository.save(task);
    }


    @Transactional(readOnly = true)
    public Page<TaskBean> list(TaskFilter taskFilter, Pageable pageable) {
        Page<Task> tasks = taskRepository.findAll(isSuitableTask(taskFilter), pageable);
        return toBeanPage(tasks);
    }

    @Override
    protected TaskBean toBean(Task entity) {
        TaskBean bean = new TaskBean();
        bean.setId(entity.getId());
        bean.setProjectId(entity.getProject().getId());
        bean.setTitle(entity.getTitle());
        bean.setDescription(entity.getDescription());
        bean.setPriority(entity.getPriority());
        bean.setStatus(entity.getStatus());
        copyDates(entity, bean);
        return bean;
    }

    @Override
    protected void beanToEntity(TaskBean bean, Task entity) {
        entity.setTitle(bean.getTitle());
        entity.setDescription(bean.getDescription());
        entity.setPriority(bean.getPriority());
        entity.setStatus(bean.getStatus());
    }

    private static Specification<Task> isSuitableTask(TaskFilter taskFilter) {
        return (Specification<Task>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (taskFilter.getCreatedStart() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("created"), taskFilter.getCreatedStart()));
            }
            if (taskFilter.getCreatedEnd() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("created"), taskFilter.getCreatedEnd()));
            }
            if (taskFilter.getModifiedStart() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("modified"), taskFilter.getModifiedStart()));
            }
            if (taskFilter.getModifiedEnd() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("modified"), taskFilter.getModifiedEnd()));
            }
            if (taskFilter.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), taskFilter.getStatus()));
            }
            if (taskFilter.getPriority() != null) {
                predicates.add(criteriaBuilder.equal(root.get("priority"), taskFilter.getPriority()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
