package com.denver7074.bot.model;

import com.denver7074.bot.model.common.IdentityEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
    public String toString() {
        return email + "\n";
    }
}

