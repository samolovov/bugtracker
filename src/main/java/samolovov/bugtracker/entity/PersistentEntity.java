package samolovov.bugtracker.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@MappedSuperclass
@Getter
@Setter
public abstract class PersistentEntity extends AbstractPersistable<Integer> {

    @Column(name = "created", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @Column(name = "deleted")
    private boolean deleted;

    @PrePersist
    protected void prePersist() {
        setCreated(new Date());
        preUpdate();
    }

    @PreUpdate
    private void preUpdate() {
        setModified(new Date());
    }

}
