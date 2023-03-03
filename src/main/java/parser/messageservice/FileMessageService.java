package parser.messageservice;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import parser.model.Verification;
import parser.model.enums.BotState;
import parser.services.UserService;
import parser.services.file.ImportService;
import parser.services.verification.VerificationService;

import java.io.FileInputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class FileMessageService {

    VerificationService verificationService;

    UserService userService;

    ImportService importService;

    public SendDocument sendImportFile(Long userId) {
        BotState botState = userService.findUserById(userId).getBotState();
        String caption = null;
        if (botState.equals(BotState.CONTROL_MEASURING_INSTRUMENT)) {
            int dayMailing = userService.findUserById(userId).getDayMailing();
            caption = BotState.CONTROL_MEASURING_INSTRUMENT.getValue() + dayMailing;
        }
        FileInputStream file = documentLoad(userId);
        return sendDocument(userId, caption, file, "Средства измерений");
    }

    public FileInputStream documentLoad(Long userId) {
        List<Verification> allInstrument = verificationService.findAllInstrument(userId);
        return importService.importToExcel(allInstrument, userId);
    }

    public SendDocument sendDocument(Long userId, String caption, FileInputStream file, String name) {
        return SendDocument.builder()
                .chatId(userId)
                .caption(caption)
                .document(new InputFile(file, String.format("%s.xlsx", name)))
                .build();
    }

}
