package com.denver7074.bot.service.excel;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class ExcelUtils {

    static final DateTimeFormatter EXCEL_DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    static final DateTimeFormatter EXCEL_DT_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    static CellStyle getHeaderStyle(Workbook workbook) {
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        CellStyle dataStyle = getDataStyle(workbook);
        dataStyle.setFont(font);
        dataStyle.setFillForegroundColor(IndexedColors.SEA_GREEN.getIndex());
        dataStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return dataStyle;
    }

    static CellStyle getDataStyle(Workbook workbook) {
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setWrapText(true);
        dataStyle.setBorderBottom(BorderStyle.HAIR);
        dataStyle.setBorderLeft(BorderStyle.HAIR);
        dataStyle.setBorderRight(BorderStyle.HAIR);
        dataStyle.setBorderTop(BorderStyle.HAIR);
        dataStyle.setAlignment(HorizontalAlignment.CENTER);
        dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        return dataStyle;
    }

    static String patternFormat(Object o) {
        if (Objects.isNull(o)) return StringUtils.EMPTY;
        if (o.getClass().equals(LocalDate.class)) {
            return ((LocalDate) o).format(EXCEL_DATE_FORMAT);
        } else if (o.getClass().equals(LocalDateTime.class)) {
            return ((LocalDateTime) o).format(EXCEL_DT_FORMAT);
        } else if (o.getClass().equals(Double.class)) {
            return String.format("%.2f", (Double) o);
        } else if (o.getClass().equals(Float.class)) {
            return String.format("%.2f", (Float) o);
        }
        return o.toString();
    }

}
