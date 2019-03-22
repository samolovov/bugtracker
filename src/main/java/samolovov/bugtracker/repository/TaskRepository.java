package samolovov.bugtracker.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;
import samolovov.bugtracker.entity.Project;
import samolovov.bugtracker.entity.Task;

@Transactional(readOnly = true)
public interface TaskRepository extends JpaSpecificationExecutor<Task>, AbstractRepository<Task> {
    Page<Task> findByProject(Project project, Pageable pageable);
    Task findByIdAndProjectId(int id, int projectId);
}
