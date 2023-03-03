package parser.services.file;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import parser.model.Verification;
import parser.model.enums.BotState;
import parser.utils.QRUtil;
import parser.services.UserService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ImportService {
    UserService userService;

    @SneakyThrows
    public FileInputStream importToExcel(List<Verification> list, Long userId) {
        XSSFWorkbook book = new XSSFWorkbook(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("Средства измерений.xlsx")));
        XSSFSheet sheet = book.getSheet("Поверка");
        if (!list.isEmpty() && !userService.findUserById(userId).getBotState().equals(BotState.FIND_AUTO)) {
            int rowNum = 1;
            for (Verification v : list) {
                Row row = sheet.createRow(rowNum++);
                createList(v, row, book, sheet, rowNum);
            }
        }
        for (int i = 0; i < 6; i++) {
            sheet.autoSizeColumn(i);
        }

        File result = File.createTempFile("Средства измерений", ".xlsx");
        FileOutputStream out = new FileOutputStream(result);
        book.write(out);
        out.close();
        return new FileInputStream(result);
    }

    private void createList(Verification v, Row row, XSSFWorkbook book, XSSFSheet sheet, int rowNum) {
        Cell cell = row.createCell(0);
        cell.setCellStyle(StyleUtil.styleCell(Style.TEXT_STYLE, book));
        cell.setCellFormula(StyleUtil.hrefStyle(v.getHref(), v.getIdVerification()));

        cell = row.createCell(1);
        cell.setCellStyle(StyleUtil.styleCell(Style.TEXT_STYLE, book));
        cell.setCellValue(v.getMiType());

        cell = row.createCell(2);
        cell.setCellStyle(StyleUtil.styleCell(Style.TEXT_STYLE, book));
        cell.setCellValue(v.getNumber());

        cell = row.createCell(3);
        cell.setCellStyle(StyleUtil.styleCell(Style.TEXT_STYLE, book));
        cell.setCellValue(v.getMitNumber());

        cell = row.createCell(4);
        cell.setCellStyle(StyleUtil.styleCell(Style.DATA_STYLE, book));
        cell.setCellValue(v.getValidDate());


        cell = row.createCell(5);
        cell.setCellStyle(StyleUtil.styleCell(Style.DATA_STYLE, book));
        cell.setCellValue(v.getVerificationDate());

        int pictureIdx = book.addPicture(QRUtil.generateQR(v.getHref()), Workbook.PICTURE_TYPE_PNG);
        XSSFDrawing drawing = sheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = new XSSFClientAnchor();
        anchor.setCol1(6); // Sets the column (0 based) of the first cell.
        anchor.setCol2(7); // Sets the column (0 based) of the Second cell.
        int rowNum1 = rowNum - 1;
        anchor.setRow1(rowNum1); // Sets the row (0 based) of the first cell.
        anchor.setRow2(rowNum); // Sets the row (0 based) of the Second cell.
        drawing.createPicture(anchor, pictureIdx);
        row.setHeightInPoints(60);
        sheet.setColumnWidth(6, 4000);
    }


}
