package samolovov.bugtracker.repository;

import org.springframework.transaction.annotation.Transactional;
import samolovov.bugtracker.entity.Project;

@Transactional(readOnly = true)
public interface ProjectRepository extends AbstractRepository<Project> {
}
