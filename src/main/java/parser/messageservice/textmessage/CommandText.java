package parser.messageservice.textmessage;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import parser.messageservice.TextMessageService;
import parser.model.enums.BotState;
import parser.services.SpeechToText;
import parser.services.UserService;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CommandText {
    UserService userService;
    TextMessageService textMessageService;
    SpeechToText speechToText;

    public SendMessage handleUpdate(Update update) {
        return handleInputMessage(update.getMessage());
    }

    @SneakyThrows
    private SendMessage handleInputMessage(Message message) {
        Long userId = message.getFrom().getId();
        String name = message.getFrom().getFirstName();
        String command = "";
        if (message.hasText()) {
            command = message.getText();
        }
        if (message.hasVoice()) {
            command = speechToText.commandForBot(message.getVoice().getFileId());
        }
        String answer = "не понимаю";
        BotState botState = null;
        if (userService.findUserById(userId) != null) {
            if (userService.findByDeletedTrueAndUserId(userId)){
                userService.recoverUser(userId);
            }
            else {
                botState = userService.findUserById(userId).getBotState();
            }
        }

        switch (command) {
            case "/start" -> {
                botState = BotState.SHOW_MAIN_MENU;
                answer = BotState.SHOW_MAIN_MENU.getValue();
                if (userService.findUserById(userId) == null) {
                    userService.saveNewUser(userId, name);
                }
            }
            case "/verification" -> {
                answer = BotState.WORK_WITH_VERIFICATION.getValue();
                botState = BotState.WORK_WITH_VERIFICATION;
            }
            case "/help" -> {
                botState = BotState.SHOW_HELP_MENU;
                answer = BotState.SHOW_HELP_MENU.getValue();
            }
            case "/audit" -> {
                botState = BotState.AUDIT;
                answer = BotState.AUDIT.getValue();
            }
            case "/advertisement" -> {
                botState = BotState.ADVERTISEMENT;
                answer = BotState.ADVERTISEMENT.getValue();
            }
            case "/profile" -> {
                botState = BotState.ABOUT_ME;
                answer = BotState.ABOUT_ME.getValue() + userService.findUserById(userId).toString();
            }
            default -> {
                answer = textMessageService.messageDefault(command, userId, answer);
            }
        }
        userService.updateBotState(userId, botState);
        return textMessageService.sendMessage(userId, answer);
    }

}
