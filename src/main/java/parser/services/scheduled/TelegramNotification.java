package parser.services.scheduled;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import parser.TelegrammBot;
import parser.messageservice.FileMessageService;
import parser.model.Constant;
import parser.model.User;
import parser.model.Verification;
import parser.model.enums.BotState;
import parser.services.UserService;
import parser.services.file.ImportService;
import parser.services.verification.VerificationService;

import java.io.FileInputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TelegramNotification {

    UserService userService;
    TelegrammBot telegrammBot;
    ImportService importService;
    FileMessageService fileMessageService;
    VerificationService verificationService;

    @Scheduled(cron = "${cron.finishVerification}")
    public void notificationFinishVerification() {
        Map<User, List<Verification>> mapBeforeDate = verificationService.findBeforeFinishVerification(LocalDate.now());
        for (User u : mapBeforeDate.keySet()) {
            executeAsyncDocument(u, mapBeforeDate.get(u), Constant.FIRST_ANSWER, "Ты сдал это на поверку?");
        }
    }

    @Scheduled(cron = "${cron.finishVerification}")
    public void updateVerification() {
        Map<User, List<Verification>> mapUpdate = verificationService.autoUpdateVerification(LocalDate.now());
        for (User u : mapUpdate.keySet()) {
            executeAsyncDocument(u, mapUpdate.get(u), Constant.UPDATE_ANSWER, "Обновилось сегодня");
        }
    }

    @Scheduled(cron = "${cron.userActivate}")
    public void userActivate() {
        List<User> byLocalTime = userService.findByLocalTime(LocalDateTime.now(), 30, BotState.SHOW_MAIN_MENU);
        for (User u : byLocalTime) {
            executeAsyncTextMessage(u);
        }
    }

    @Scheduled(cron = "${cron.deleteUser}")
    public void deleteUser() {
        userService.deleteUser();
    }

    @SneakyThrows
    private void executeAsyncTextMessage(User u) {
        Long userId = u.getUserId();
        userService.updateBotState(userId, BotState.SHOW_MAIN_MENU);
        SendMessage build = SendMessage.builder()
                .text(BotState.SHOW_MAIN_MENU.getValue())
                .chatId(userId)
                .build();
        telegrammBot.executeAsync(build);
    }

    private void executeAsyncDocument(User u, List<Verification> list, String caption, String nameFile) {
        Long userId = u.getUserId();
        FileInputStream file = importService.importToExcel(list, userId);
        SendDocument sendDocument = fileMessageService.sendDocument(userId, caption, file, nameFile);
        telegrammBot.executeAsync(sendDocument);
    }
}
