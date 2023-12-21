package com.denver7074.bot.model;

import com.denver7074.bot.utils.Utils;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Accessors(chain = true)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class Subscriber implements Serializable {

    @Id
    Long id;
    @Enumerated(EnumType.STRING)
    BotState botState = BotState.SHOW_START_MENU;
    String firstName;
    Boolean deleted = Boolean.FALSE;
    @CreatedDate
    LocalDate registration;
    @LastModifiedDate
    LocalDateTime activity;

    @Override
    public String toString() {
        return  "id: " + id + "\n" +
                "имя: " + firstName + "\n" +
                "дата регистрации в боте: " + Utils.convertDate(registration);
    }
}
