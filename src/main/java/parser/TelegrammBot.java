package parser;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import parser.messageservice.CallbackQueryService;
import parser.messageservice.FileMessageService;
import parser.messageservice.textmessage.CommandText;
import parser.configuration.BotConfig;
import parser.model.User;
import parser.model.enums.BotState;
import parser.services.UserService;
import parser.services.file.FileService;
import parser.services.keyboards.Menu;
import parser.services.scheduled.TelegramNotification;

import java.time.LocalDateTime;
import java.util.Set;


@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class TelegrammBot extends TelegramLongPollingBot {
    BotConfig botConfig;
    FileService fileService;
    CommandText commandText;
    UserService userService;
    CallbackQueryService callbackQueryService;
    FileMessageService fileMessageService;

    @Override
    public String getBotUsername() {
        return botConfig.getName();
    }
    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {
        execute(Menu.menu());
        if (update.hasCallbackQuery()) {
            Long userId = update.getCallbackQuery().getFrom().getId();
            execute(callbackQueryService.handleCallbackText(update));
            BotState botState = userService.findUserById(userId).getBotState();
            if (BotState.callBack.contains(botState)){
                execute(fileMessageService.sendImportFile(userId));
            }
        }
        if (update.getMessage() != null) {
            if (update.getMessage().hasText() || update.getMessage().hasVoice()) {
                execute(commandText.handleUpdate(update));
            }
            //нужно написать обработку проверку тип загруженного файла
            if (update.getMessage().hasDocument()) {
                Long id = update.getMessage().getFrom().getId();
                BotState botState = userService.findUserById(id).getBotState();
                if (BotState.callBack.contains(botState)) {
                    fileService.readExcelInstrument(update, id);
                    execute(fileMessageService.sendImportFile(id));
                }
            }
        }
    }
    //каждый день в 10:00
//    @SneakyThrows
//    @Scheduled(cron = "0 0 10 * * *")
//    public void notification() {
//        if (telegramNotification.notificationFinishVerification() != null) {
//            execute(telegramNotification.notificationFinishVerification());
//        }
//        if (telegramNotification.updateVerification() != null){
//            execute(telegramNotification.updateVerification());
//        }
//    }


}
