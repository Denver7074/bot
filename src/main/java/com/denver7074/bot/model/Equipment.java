package com.denver7074.bot.model;

import com.denver7074.bot.model.common.IdentityEntity;
import com.denver7074.bot.service.CrudService;
import com.denver7074.bot.service.excel.ExcelCell;
import com.denver7074.bot.utils.Utils;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

import static com.denver7074.bot.utils.Constants.FGIS_ARCHIN;
import static com.denver7074.bot.utils.Utils.convertDate;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;

@Data
@Entity
@Accessors(chain = true)
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class Equipment extends IdentityEntity {

    @JsonAlias("vri_id")
    @ExcelCell(valueCell = "Номер записи ФГИС Аршин", numberCell = 1)
    String idVerification;
    @JsonAlias("mi.mitnumber")
    @ExcelCell(valueCell = "Номер в ФГРСИ", numberCell = 2)
    String mitNumber;
    @JsonAlias("mi.number")
    @ExcelCell(valueCell = "Заводской номер", numberCell = 3)
    String number;
    @JsonAlias("valid_date")
    @ExcelCell(valueCell = "Дата окончания поверки", numberCell = 4)
    LocalDate validDate;
    @JsonAlias("verification_date")
    @ExcelCell(valueCell = "Дата поверки", numberCell = 5)
    LocalDate verificationDate;
    @JsonAlias("mi.mititle")
    @ExcelCell(valueCell = "Название СИ", numberCell = 6)
    String name;
    @JsonAlias("mi.modification")
    @ExcelCell(valueCell = "модификация", numberCell = 7)
    String modification;
    @JsonAlias("org_title")
    @ExcelCell(valueCell = "Организация поверитель", numberCell = 8)
    String orgTitle;
    @JsonAlias("result_text")
    @ExcelCell(valueCell = "Пригодность", numberCell = 9)
    String result;
    @ExcelCell(valueCell = "Ссылка на ФГИС аршин", numberCell = 10)
    String href;
    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    Subscriber user;

    @JsonProperty("idVerification")
    public void setIdVerification(String idVerification) {
        this.idVerification = idVerification;
        this.href = FGIS_ARCHIN + idVerification;
    }

    @Override
    public String toString() {
        return  "Название СИ: " +  name + '\n' +
                "Модификация: " + modification + '\n' +
                "Номер записи: " + idVerification + '\n' +
                "Номер ФГРСИ: " + mitNumber + '\n' +
                "Заводско номер: " + number + '\n' +
                "Дата окончания поверки: " + convertDate(validDate) + '\n' +
                "Дата поверки: " + convertDate(verificationDate) + '\n' +
                "Организация поверитель: " + orgTitle + '\n' +
                result + '\n' +
                href;
    }
//    public void reachTransient(CrudService crudService) {
//
//    }
}
