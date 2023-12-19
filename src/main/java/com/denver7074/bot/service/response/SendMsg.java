package com.denver7074.bot.service.response;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SendMsg {

    Long userId;
    String textMsg;
    Integer messageId;
    InputStream file;
    Collection<String> textButtons;

    public SendMsg(String textMsg, Long userId, InputStream file) {
        this.textMsg = textMsg;
        this.file = file;
        this.userId = userId;
    }

    public SendMsg(String textMsg, Long userId, Collection<String> textButtons, Integer messageId) {
        this.textMsg = textMsg;
        this.userId = userId;
        this.textButtons = textButtons;
        this.messageId = messageId;
    }

    public SendMsg(String textMsg, Long userId, Collection<String> textButtons) {
        this.textMsg = textMsg;
        this.userId = userId;
        this.textButtons = textButtons;
    }

    public SendMessage getMsg() {
        return SendMessage
                .builder()
                .chatId(getUserId())
                .text(getTextMsg())
                .replyMarkup(getKeyBoards(textButtons))
                .build();
    }

    public EditMessageText getEditMsg() {
        return EditMessageText
                .builder()
                .chatId(userId)
                .text(textMsg)
                .messageId(messageId)
                .replyMarkup(getKeyBoards(textButtons))
                .build();
    }

    public SendDocument getDocument() {
        return SendDocument
                .builder()
                .chatId(userId)
                .caption(textMsg)
                .document(new InputFile(file, String.format("%s.xlsx", "Поверка")))
                .build();
    }

    private static InlineKeyboardMarkup getKeyBoards(Collection<String> textButtons) {
        if (isEmpty(textButtons)) return null;
        List<InlineKeyboardButton> buttons = new ArrayList<>(textButtons.size());
        for (String l : textButtons) {
            buttons.add(InlineKeyboardButton.builder()
                            .text(l)
                            .callbackData(l)
                            .build());
        }
        return InlineKeyboardMarkup
                .builder()
                .keyboard(List.of(buttons))
                .build();
    }
}
