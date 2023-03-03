package parser.model;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDate;


@Entity
@Getter
@Setter
@Audited
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class Verification {

    @Id
    @JsonAlias("vri_id")
    String idVerification;
    @JsonAlias("mi.mitnumber")
    String mitNumber;
    @JsonAlias("mi.number")
    String number;
    @JsonAlias("valid_date")
    LocalDate validDate;
    @JsonAlias("verification_date")
    LocalDate verificationDate;
    @JsonAlias("mi.modification")
    String miType;
    @JsonAlias("org_title")
    String orgTitle;
    Boolean applicability;
    String href;
    LocalDate writingAboutVerification;
    LocalDate updateVerification;
    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    User user;

    @Override
    public String toString() {
        String result = "Отрицательный";
        if (Boolean.TRUE.equals(applicability)) {
            result = "Положительный";
        }
        return "Номер записи о поверке: " + idVerification + "\n" +
                "Наименование СИ: " + miType + "\n" +
                "Заводской номер: " + number + "\n" +
                "ГРСИ: " + mitNumber + "\n" +
                "Дата поверки: " + Constant.FORMATTER.format(verificationDate) + "\n" +
                "Дата окончания поверки: " + Constant.FORMATTER.format(validDate) + "\n" +
                "Результат поверки: " + result + "\n" +
                "Поверитель: " + orgTitle + "\n" +
                Constant.HREF + idVerification + "\n\n";
    }
}
