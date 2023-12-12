package com.denver7074.bot.model.common;

import java.io.Serializable;

public interface GenericIdProjection<ID> extends Serializable {

    ID getId();
}
