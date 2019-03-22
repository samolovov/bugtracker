package samolovov.bugtracker.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import samolovov.bugtracker.entity.PersistentEntity;

@NoRepositoryBean
public interface AbstractRepository<T extends PersistentEntity> extends JpaRepository<T, Integer> {
    T findOneById(Integer id);
}
