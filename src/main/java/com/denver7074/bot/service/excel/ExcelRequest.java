package com.denver7074.bot.service.excel;

import com.denver7074.bot.model.common.IdentityEntity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExcelRequest extends IdentityEntity {

    @ExcelCell(valueCell = "Номер в ФГРСИ")
    String mitNumber;
    @ExcelCell(valueCell = "Заводской номер")
    String number;
    @ExcelCell(valueCell = "Название СИ")
    String name;
    @ExcelCell(valueCell = "Модификация")
    String modification;
}
