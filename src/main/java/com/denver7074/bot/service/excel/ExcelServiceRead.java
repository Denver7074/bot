package com.denver7074.bot.service.excel;

import com.denver7074.bot.model.BotState;
import com.denver7074.bot.model.Equipment;
import com.denver7074.bot.model.Subscriber;
import com.denver7074.bot.service.CrudService;
import com.denver7074.bot.service.verification.VerificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ExcelServiceRead {

    CrudService crudService;
    ModelMapper modelMapper;
    VerificationService verificationService;

    @SneakyThrows
    public void readExcel(ByteArrayInputStream byteArrayInputStream, Subscriber user) {
        try (Workbook workbook = new XSSFWorkbook(byteArrayInputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            System.out.println(sheet.getLastRowNum());
            int len = sheet.getLastRowNum() + 1;
            List<ExcelRequest> excelRequests = new ArrayList<>(len);
            for (int i = 1; i < len; i++) {
                Row row = sheet.getRow(i);
                excelRequests.add(new ExcelRequest()
                        .setMitNumber(row.getCell(0).toString().replaceAll(" ", ""))
                        .setNumber(String.valueOf(row.getCell(1).getRichStringCellValue()))
                        .setName(row.getCell(2).toString())
                        .setModification(row.getCell(3).toString())
                );
            }

            for (ExcelRequest ex : excelRequests) {
                Equipment eq = crudService.find(Equipment.class, Map.of(Equipment.Fields.userId, user.getId(),
                        Equipment.Fields.mitNumber, ex.getMitNumber(),
                        Equipment.Fields.number, ex.getNumber()
                ), null).stream().findFirst().orElse(null);
                workWithFile(user, eq, ex);
            }
        }
    }

    private void workWithFile(Subscriber user, Equipment eq, ExcelRequest ex) {
        if (user.getBotState().equals(BotState.VERIFICATION_UPDATE) && isNotEmpty(eq)) {
            modelMapper.map(ex, eq);
            crudService.update(user.getId(), eq, eq.getId(), Equipment.class);
        } else if (user.getBotState().equals(BotState.VERIFICATION_FIND) && isEmpty(eq)) {
            Equipment lastVerification = verificationService.findLastVerification(user.getId(), ex.getMitNumber(), ex.getNumber());
            crudService.create(lastVerification);
        }
    }
}
