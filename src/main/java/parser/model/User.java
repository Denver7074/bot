package parser.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import parser.model.enums.BotState;
import parser.utils.TextUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Audited
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    Long userId;
    String name;
    BotState botState;
    int countSi;
    int dayMailing = 30;
    boolean deleted = false;
    LocalDate whenDeleted;
    boolean ignoreUser = false;
    @CreatedDate
    LocalDate registration;
    LocalDateTime activity;
    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER, mappedBy = "user")
    List<Verification> verifications = new ArrayList<>();
    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER, mappedBy = "user")
    List<Mailing> contacts = new ArrayList<>();

    @Override
    public String toString() {
        String text = TextUtil.stringUtil(contacts);
        if (contacts.isEmpty()) {
            text = "На данный момент у вас не подключено оповещение по e-mail. Для подключения зайдите в раздел /verification";
        }
        return "Id пользователя: " + userId + "\n" +
                "Имя пользователя: " + name + "\n" +
                "Количество СИ на контроле: " + countSi + "\n" +
                "Дата регистрации в телеграмм боте: " + registration + "\n" +
                "Почты, на которые подключено уведомление:\n" + text;
    }

    public String getNameAndUserId(){
        return "Имя: " + this.name + " userId: " + userId + "\n";
    }

}
