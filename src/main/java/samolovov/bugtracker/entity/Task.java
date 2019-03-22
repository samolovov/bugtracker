package samolovov.bugtracker.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;
import samolovov.bugtracker.enums.TaskStatus;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

@Entity
@Table(name = "task")
@Getter
@Setter
@Where(clause = "deleted=false")
public class Task extends PersistentEntity {
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @Column(name = "priority")
    private Integer priority;

    @ManyToOne
    @JoinColumn(
            name = "project_id",
            foreignKey = @ForeignKey(name = "project_fk")
    )
    private Project project;

    @Override
    @PrePersist
    protected void prePersist() {
        super.prePersist();
        status = TaskStatus.NEW;
    }
}
