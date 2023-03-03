package parser.services.file;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import parser.model.Verification;
import parser.model.enums.BotState;
import parser.services.UserService;
import parser.services.verification.VerificationApiService;
import parser.services.verification.VerificationService;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class FileService {
    ExportService exportService;
    VerificationApiService verificationApiService;
    VerificationService verificationService;
    UserService userService;

    public void readExcelInstrument(Update update, Long userId) throws IOException {
        BotState botState = userService.findUserById(userId).getBotState();
        String fileId = update.getMessage().getDocument().getFileId();
        byte[] bytes = exportService.loadFile(fileId);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Workbook workbook = new XSSFWorkbook(byteArrayInputStream);
        Sheet sheet = workbook.getSheetAt(0);
        System.out.println(sheet.getLastRowNum());
        for (int i = 1; i < sheet.getLastRowNum() + 1; i++) {
            String mitNumber = sheet.getRow(i).getCell(3).toString().replaceAll(" ", "");
            var number = String.valueOf(sheet.getRow(i).getCell(2).getRichStringCellValue()).replaceAll(" ", "");
            String miType = sheet.getRow(i).getCell(1).getStringCellValue();
            Verification instrument = verificationService.findInstrument(mitNumber, number, userId);
            if (botState.equals(BotState.DELETE_AUTO)) {
                verificationService.deleteVerification(instrument,userId);
            } else {
                if (botState.equals(BotState.FIND_AUTO)) {
                    userService.updateBotState(userId, BotState.CONTROL_MEASURING_INSTRUMENT);
                }
                if (instrument == null) {
                    Verification v = verificationApiService.api(mitNumber, number).get(0);
                    if (!v.getMiType().equals(miType) && !miType.isEmpty()) {
                        v.setMiType(miType);
                    }
                    verificationService.saveVerification(userId, v);
                } else {
                    if (!instrument.getMiType().equals(miType) && !miType.isEmpty()) {
                        verificationService.updateMiType(instrument.getIdVerification(), miType);
                    }
                }
            }
        }
    }

}
