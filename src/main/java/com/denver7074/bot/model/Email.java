package com.denver7074.bot.model;

import com.denver7074.bot.model.common.IdentityEntity;
import com.denver7074.bot.service.CrudService;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;

import java.util.Collections;
import java.util.List;

import static com.denver7074.bot.utils.Constants.pattern;
import static com.denver7074.bot.utils.errors.Errors.E003;
import static org.apache.commons.lang3.BooleanUtils.isFalse;

@Data
@Entity
@FieldNameConstants
@Accessors(chain = true)
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Email extends IdentityEntity {

    String email;
    Long userId;

    @Override
    public void validate(CrudService crudService) {
        E003.thr(isFalse(pattern.matcher(getEmail()).matches()),
                getUserId(), crudService.findButtonEmail(getUserId()),
                getEmail());
    }

    @Override
    public String toString() {
        return email + "\n";
    }
}

