package com.denver7074.bot.service;

import com.denver7074.bot.model.Email;
import com.denver7074.bot.model.Subscriber;
import com.denver7074.bot.model.common.IdentityEntity;
import com.denver7074.bot.utils.Utils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.data.util.CastUtils;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.util.*;

import static com.denver7074.bot.utils.errors.Errors.E001;
import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ObjectUtils.isEmpty;

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

    public <E, ID> E findNullable(Class<E> clazz, ID id) {
        return Optional.ofNullable(entityManager.find(clazz, id)).orElse(null);
    }

    public <E, D> E create(D dto, Class<E> clazz) {
        E merge = entityManager.merge(toMap(clazz, dto));
        return CastUtils.cast(merge);
    }


    public <E extends IdentityEntity> E create(E entity) {
        IdentityEntity reach = entity.reach(this);
        IdentityEntity merge = entityManager.merge(reach);
        return CastUtils.cast(merge);
    }

    public <E, D> E update(Long userId, D source, Long id, Class<E> clazz) {
        E target = findNullable(clazz, id);
        E001.thr(isEmpty(target), userId, clazz.getSimpleName(), id);
        modelMapper.map(source, target);
        entityManager.persist(target);
        return CastUtils.cast(target);
    }

    //можно ещё поработать над фильтрами и сортировками
    public <E> List<E> find(Class<E> clazz, Map<String, Object> filter, Pair<String, LocalDate> dateAfter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<E> query = cb.createQuery(clazz);
        Root<E> root = query.from(clazz);
        List<Predicate> predicates = new ArrayList<>();
        for (Map.Entry<String, Object> entry : filter.entrySet()) {
            String field = entry.getKey();
            Object value = entry.getValue();
            predicates.add(cb.equal(root.get(field), value));
        }
        if (nonNull(dateAfter)) {
            predicates.add(cb.lessThanOrEqualTo(root.get(dateAfter.getFirst()), dateAfter.getSecond()));
        }
        query.where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(query).getResultList();
    }

    public List<String> find(Subscriber user) {
        List<String> buttonEmail = Utils.safeGet(() -> findButtonEmail(user));
        return isEmpty(buttonEmail) ? emptyList() : buttonEmail;
    }

    public List<String> findButtonEmail(Subscriber user) {
        return find(Email.class, Collections.singletonMap(Email.Fields.userId, user.getId()), null)
                .stream()
                .map(Email::getEmail)
                .toList();
    }

    public <T extends IdentityEntity> void delete(T entity, Class<T> clazz) {
        if (ObjectUtils.isEmpty(entity)) return;
        T reference = entityManager.getReference(clazz, entity.getId());
        entityManager.remove(reference);
    }

}
