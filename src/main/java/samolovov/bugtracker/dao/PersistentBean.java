package samolovov.bugtracker.dao;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public abstract class PersistentBean {
    private Integer id;
    private Date createDate;
    private Date modifyDate;
    private boolean deleted;
}
