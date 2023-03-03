package parser.messageservice;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import parser.messageservice.callback.VerificationCallback;
import parser.model.enums.BotState;
import parser.services.UserService;
import parser.services.keyboards.InlineButton;


@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CallbackQueryService {
    UserService userService;
    InlineButton inlineButton;
    VerificationCallback verificationCallback;

    public EditMessageText handleCallbackText(Update update) {
        String data = update.getCallbackQuery().getData();
        Long userId = update.getCallbackQuery().getFrom().getId();
        String text = verificationCallback.verificationClick(data, userId);
        BotState botState = userService.findUserById(userId).getBotState();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        InlineKeyboardMarkup d = null;
        if (BotState.handleCallback.contains(botState)) {
            d = InlineKeyboardMarkup
                    .builder()
                    .keyboard(inlineButton.getButton(userId))
                    .build();
        }
        return EditMessageText.builder()
                .text(text)
                .chatId(userId)
                .messageId(messageId)
                .replyMarkup(d).build();
    }


}
