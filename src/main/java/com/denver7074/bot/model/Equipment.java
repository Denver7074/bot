package com.denver7074.bot.model;

import com.denver7074.bot.model.common.IdentityEntity;
import com.denver7074.bot.service.excel.ExcelCell;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDate;

import static com.denver7074.bot.utils.Constants.FGIS_ARCHIN;
import static com.denver7074.bot.utils.Utils.convertDate;

@Data
@Entity
@FieldNameConstants
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Equipment extends IdentityEntity {

    @JsonAlias("vri_id")
    @ExcelCell(valueCell = "Номер записи ФГИС Аршин")
    String idVerification;
    @JsonAlias("mi.mitnumber")
    @ExcelCell(valueCell = "Номер в ФГРСИ")
    String mitNumber;
    @JsonAlias("mi.number")
    @ExcelCell(valueCell = "Заводской номер")
    String number;
    @JsonAlias("valid_date")
    @ExcelCell(valueCell = "Дата окончания поверки")
    LocalDate validDate;
    @JsonAlias("verification_date")
    @ExcelCell(valueCell = "Дата поверки")
    LocalDate verificationDate;
    @JsonAlias("mi.mititle")
    @ExcelCell(valueCell = "Название СИ")
    String name;
    @JsonAlias("mi.modification")
    @ExcelCell(valueCell = "Модификация")
    String modification;
    @JsonAlias("org_title")
    @ExcelCell(valueCell = "Организация поверитель")
    String orgTitle;
    @JsonAlias("result_text")
    @ExcelCell(valueCell = "Пригодность")
    String result;
    @ExcelCell(valueCell = "Ссылка на ФГИС аршин")
    String href;
    Long userId;

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
}
