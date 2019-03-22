package samolovov.bugtracker.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import samolovov.bugtracker.dao.PersistentBean;
import samolovov.bugtracker.entity.PersistentEntity;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public abstract class PersistentService<Bean extends PersistentBean, Entity extends PersistentEntity> {
    public abstract Bean create(Bean bean);
    public abstract Bean update(Bean bean);

    protected abstract Bean toBean(Entity entity);
    protected abstract void beanToEntity(Bean bean, Entity entity);

    void copyDates(Entity entity, Bean bean) {
        bean.setCreateDate(new Date(entity.getCreated().getTime()));
        bean.setModifyDate(new Date(entity.getModified().getTime()));
    }

    Page<Bean> toBeanPage(Page<Entity> entities) {
        List<Bean> beans = entities.getContent().stream().map(this::toBean).collect(Collectors.toList());
        return new PageImpl<>(beans, entities.getPageable(), entities.getTotalElements());
    }

}
