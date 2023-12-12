package com.denver7074.bot.model.common;

import com.denver7074.bot.service.CrudService;

public interface ReachableDTO extends GenericIdProjection<Long> {

    IdentityEntity reach(CrudService crudService);

    default void validate(CrudService crudService) {
    }

    default void reachTransient(CrudService crudService) {
    }

}
