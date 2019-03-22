package samolovov.bugtracker.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "project")
@Getter
@Setter
@Where(clause = "deleted=false")
public class Project extends PersistentEntity {
    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

}
