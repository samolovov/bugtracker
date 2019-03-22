package samolovov.bugtracker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import samolovov.bugtracker.dao.ProjectBean;
import samolovov.bugtracker.entity.Project;
import samolovov.bugtracker.entity.Task;
import samolovov.bugtracker.repository.ProjectRepository;
import samolovov.bugtracker.repository.TaskRepository;

import java.util.List;

import static samolovov.bugtracker.enums.ServerMessage.PROJECT_NOT_FOUND;
import static samolovov.bugtracker.util.Preconditions.checkNotNull;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService extends PersistentService<ProjectBean, Project> {
    private static final PageRequest PAGEABLE_ALL = PageRequest.of(0, Integer.MAX_VALUE);
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    @Transactional(readOnly = true)
    public Page<ProjectBean> list(Pageable pageable) {
        Page<Project> projects = projectRepository.findAll(pageable);
        return toBeanPage(projects);
    }

    @Transactional(readOnly = true)
    public ProjectBean get(int id) {
        Project project = projectRepository.findOneById(id);
        checkNotNull(project, PROJECT_NOT_FOUND, HttpStatus.NOT_FOUND);

        return toBean(project);
    }

    @Override
    public ProjectBean create(ProjectBean bean) {
        Project project = new Project();
        beanToEntity(bean, project);
        return toBean(projectRepository.save(project));
    }

    @Override
    public ProjectBean update(ProjectBean bean) {
        Project project = projectRepository.findOneById(bean.getId());
        checkNotNull(project, PROJECT_NOT_FOUND, HttpStatus.NOT_FOUND);

        beanToEntity(bean, project);
        return toBean(projectRepository.save(project));
    }

    public void delete(int id) {
        Project project = projectRepository.findOneById(id);
        checkNotNull(project, PROJECT_NOT_FOUND, HttpStatus.NOT_FOUND);

        List<Task> tasks = taskRepository.findByProject(project, PAGEABLE_ALL).getContent();
        tasks.forEach(task -> {
            task.setDeleted(true);
            taskRepository.save(task);
        });

        project.setDeleted(true);
        projectRepository.save(project);

    }

    @Override
    protected ProjectBean toBean(Project entity) {
        ProjectBean bean = new ProjectBean();

        bean.setId(entity.getId());
        bean.setTitle(entity.getTitle());
        bean.setDescription(entity.getDescription());
        copyDates(entity, bean);
        return bean;
    }

    @Override
    protected void beanToEntity(ProjectBean bean, Project entity) {
        entity.setTitle(bean.getTitle());
        entity.setDescription(bean.getDescription());
    }
}
