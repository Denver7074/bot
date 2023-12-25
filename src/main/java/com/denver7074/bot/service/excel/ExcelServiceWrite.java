package com.denver7074.bot.service.excel;

import lombok.SneakyThrows;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

import static org.springframework.util.ReflectionUtils.getField;

@Service
public class ExcelServiceWrite {

    @SneakyThrows
    public static InputStream writeDataToExcel(Collection<? extends Serializable> data) {
        Class<?> t = ExcelRequest.class;
        if (CollectionUtils.isNotEmpty(data)) t = data.iterator().next().getClass();
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()
        ) {
            Sheet sheet = workbook.createSheet();

            List<String> excelColumns = getAnnotatedColumnValues(t);
            CellStyle headerStyle = ExcelUtils.getHeaderStyle(workbook);
            int rowCount = writeHeaders(sheet, 0, excelColumns, headerStyle);
            CellStyle dataStyle = ExcelUtils.getDataStyle(workbook);
            if (CollectionUtils.isNotEmpty(data)) {
                for (Object dataObject : data) {
                    Row dataRow = sheet.createRow(rowCount++);
                    dataRow.setHeight(NumberUtils.SHORT_MINUS_ONE);
                    writeRowData(dataRow, dataObject, dataStyle);
                }
            }
            for (int i = 0; i < excelColumns.size(); i++) {
                sheet.autoSizeColumn(i);
            }
            workbook.write(byteArrayOutputStream);
            return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        }
    }

    static List<String> getAnnotatedColumnValues(Class<?> t) {
        Field[] fields = FieldUtils.getFieldsWithAnnotation(t, ExcelCell.class);
        List<String> excelColumns = new ArrayList<>(fields.length);
        for (Field field : fields) {
            excelColumns.add(field.getAnnotation(ExcelCell.class).valueCell());
        }
        return excelColumns;
    }

    private static int writeHeaders(Sheet sheet, int startRow, List<String> excelColumns, CellStyle headerStyle) {
        Row row = sheet.createRow(startRow);
        for (int i = 0; i < excelColumns.size(); i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(excelColumns.get(i));
            cell.setCellStyle(headerStyle);
        }
        startRow++;
        return startRow;
    }

    private static int writeRowData(Row dataRow, Object data, CellStyle dataStyle) {
        Field[] fields = FieldUtils.getFieldsWithAnnotation(
                Objects.requireNonNull(data).getClass(), ExcelCell.class);
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            Class<?> type = field.getType();
            if (BeanUtils.isSimpleValueType(type)) {
                Cell cell = dataRow.createCell(i);
                cell.setCellValue(ExcelUtils.patternFormat(getField(field, data)));
                cell.setCellStyle(dataStyle);
            } else {
                writeRowData(dataRow, getField(field, data), dataStyle);
            }
        }
        return fields.length;
    }
}
