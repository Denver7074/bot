package com.denver7074.bot.utils;

import com.denver7074.bot.model.Equipment;
import com.denver7074.bot.model.Subscriber;
import com.denver7074.bot.service.CrudService;
import com.denver7074.bot.service.verification.Verification;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.server.DelegatingServerHttpResponse;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.denver7074.bot.utils.Constants.USER_STATE;
import static com.denver7074.bot.utils.Constants.VERIFICATION;
import static org.apache.commons.lang3.ObjectUtils.isEmpty;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;


@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RedisCash {

    final RedisTemplate<Long, Object> redisTemplate;
    HashOperations <String, Long, Object> hashOperations;
    final CrudService crudService;

    public RedisCash(RedisTemplate redisTemplate, CrudService crudService) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
        this.crudService = crudService;
    }

    @PostConstruct
    public void deleteVerification() {
        delete(VERIFICATION, 806820596L);
        delete(USER_STATE, 806820596L);
    }

    public Subscriber save(Subscriber source) {
        save(USER_STATE, source.getId(), source);
        return crudService.update(source, source.getId(), Subscriber.class);
    }

    // Сначала сохраняем в кэш, а потом, если нормально, то сохраняем в бд
    public void save(Subscriber user, Equipment eq) {
        Equipment equipment = Utils.safeGet(() -> find(VERIFICATION, user.getId(), Equipment.class));
        if (isEmpty(equipment)) {
            save(VERIFICATION, user.getId(), eq);
        } else {
            equipment.setUserId(user.getId());
            crudService.create(equipment);
            delete(VERIFICATION, user.getId());
        }
    }

    public void save(String nameKey, Long userId, Object object) {
        hashOperations.put(nameKey, userId, object);
    }

    public <E> E find(String name, Long userId, Class<E> clazz) {
        return crudService.toMap(clazz, hashOperations.get(name, userId));
    }

    public Map<Long, Object> getAll(String nameKey) {
        return hashOperations.entries(nameKey);
    }

    public void delete(String nameKey, Long userId) {
        hashOperations.delete(nameKey, userId);
    }
}
