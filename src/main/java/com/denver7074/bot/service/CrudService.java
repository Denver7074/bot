package com.denver7074.bot.service;

import com.denver7074.bot.model.Subscriber;
import com.denver7074.bot.model.common.IdentityEntity;
import com.denver7074.bot.utils.Utils;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.util.CastUtils;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import static com.denver7074.bot.utils.Errors.E001;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CrudService {

    ModelMapper modelMapper;
    EntityManager entityManager;

    public <D, E> E toMap(Class<E> clazz, D dto) {
        return Utils.safeGet(() -> modelMapper.map(dto, clazz));
    }


    public <E, ID> E find(Class<E> clazz, ID id) {
        return Optional.ofNullable(entityManager.find(clazz, id))
                .orElseThrow(() -> E001.thr(clazz.getSimpleName(), id));
    }

    public <E, ID> E findNullable(Class<E> clazz, ID id) {
        return Optional.ofNullable(entityManager.find(clazz, id)).orElse(null);
    }

    public <E> List<E> findAll(Class<E> clazz) {
        return entityManager.createQuery("select e from " + clazz.getSimpleName() + " e where e.id is not null ",clazz).getResultList();
    }

    public <E, D> E create(D dto, Class<E> clazz) {
        E merge = entityManager.merge(toMap(clazz, dto));
        return CastUtils.cast(merge);
    }


    public <E extends IdentityEntity> E create(E entity) {
        entity.reachTransient(this);
        E merge = entityManager.merge(entity);
        return CastUtils.cast(merge);
    }


    public <E, D> E update(D source, Long id, Class<E> clazz) {
        E target = find(clazz, id);
        modelMapper.map(source, target);
        entityManager.persist(target);
        return CastUtils.cast(target);
    }

    @Cacheable(value = "user", key = "#id")
    public <ID> Subscriber find(ID id) {
        return find(Subscriber.class, id);
    }

    @CachePut(value = "user", key = "#id")
    public Subscriber update(Subscriber source, Long id) {
        return update(source, id, Subscriber.class);
    }

}
