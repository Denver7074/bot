package com.denver7074.bot.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    Integer countEquipment = 0;
    Integer dayMailing = 30;
    Boolean deleted = Boolean.FALSE;
    @CreatedDate
    LocalDate registration;
    @LastModifiedDate
    LocalDateTime activity;
//    @OneToMany(mappedBy = "user")
//    List<Email> emailList = new ArrayList<>();


}
