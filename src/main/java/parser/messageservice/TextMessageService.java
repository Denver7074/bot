package parser.messageservice;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import parser.model.User;
import parser.model.Verification;
import parser.model.enums.BotState;
import parser.services.MailService;
import parser.utils.TextUtil;
import parser.services.UserService;
import parser.services.ValidService;
import parser.services.cache.UserVerificationNowCache;
import parser.services.keyboards.InlineButton;
import parser.services.verification.VerificationApiService;
import parser.services.verification.VerificationService;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TextMessageService {
    MailService mailService;

    ValidService validService;
    UserService userService;
    InlineButton inlineButton;
    VerificationService verificationService;
    VerificationApiService parsingService;
    UserVerificationNowCache userVerificationNowCache;


    public SendMessage sendMessage(Long userId, String textToAnswer) {
        BotState botState = userService.findUserById(userId).getBotState();
        InlineKeyboardMarkup b = null;
        if (BotState.sendMessage.contains(botState)) {
            b = InlineKeyboardMarkup
                    .builder()
                    .keyboard(inlineButton.getButton(userId))
                    .build();
        }
        return SendMessage.builder()
                .text(textToAnswer)
                .chatId(userId)
                .replyMarkup(b)
                .build();
    }

    public String messageDefault(String command, Long userId, String answer) {
        User userById = userService.findUserById(userId);
        BotState botState = userById.getBotState();
        switch (botState) {
            case HISTORY_VERIFICATION, NOW_VERIFICATION -> answer = parsingService.find(command, botState, userId);
            case SHOW_MY_INSTRUMENT, DELETE_MANUAL -> {
                String[] text = TextUtil.commandText(command);
                Verification v = verificationService.findInstrument(text[0], text[1], userId);
                userVerificationNowCache.saveUserVerificationNow(userId, v);
                answer = v.toString();
            }
            case PLUG_EMAIL ->
                answer = validService.isValidEmail(userById, TextUtil.commandPlugEmail(command));
            case UNPLUG_EMAIL -> {
                for (String e : TextUtil.commandPlugEmail(command)) {
                    mailService.unplugEmail(userId, e);
                }
                answer = "Оповещение отключено";
            }
            case CONTROL_MEASURING_INSTRUMENT, UPDATE_DAY_MAILING -> {
                verificationService.updateDateBefore(userId, Integer.parseInt(command));
                String text = BotState.CONTROL_MEASURING_INSTRUMENT.getValue();
                if (botState.equals(BotState.UPDATE_DAY_MAILING)) {
                    text = BotState.UPDATE_DAY_MAILING.getValue();
                }
                answer = text + userById.getDayMailing();
            }
        }
        userService.updateBotState(userId, botState);
        return answer;
    }
}
