package parser.services.file;

import lombok.experimental.UtilityClass;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@UtilityClass
public class StyleUtil {
    public static CellStyle styleCell(Style style, XSSFWorkbook book) {
        CreationHelper createHelper = book.getCreationHelper();
        XSSFCellStyle cellStyle = book.createCellStyle();
        switch (style) {
            case DATA_STYLE -> cellStyle.setDataFormat(createHelper
                    .createDataFormat()
                    .getFormat("m/d/yy"));
            case TEXT_STYLE -> cellStyle.setDataFormat(createHelper
                    .createDataFormat()
                    .getFormat("@"));
            case NUMBER_STYLE -> cellStyle.setDataFormat(createHelper
                    .createDataFormat()
                    .getFormat("0.00"));
        }
        return cellStyle;
    }

    public static XSSFCellStyle border(XSSFWorkbook book) {
        XSSFCellStyle style = book.createCellStyle();
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    public static String hrefStyle(String hyperlynk, String nameCell) {
        return "HYPERLINK(\"" + hyperlynk + "\", \"" + nameCell + "\")";
    }
}
