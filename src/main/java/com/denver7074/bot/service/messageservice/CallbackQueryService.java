package com.denver7074.bot.service.messageservice;

import com.denver7074.bot.model.BotButton;
import com.denver7074.bot.model.BotState;
import com.denver7074.bot.model.Subscriber;
import com.denver7074.bot.service.CrudService;
import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CallbackQueryService {

    CrudService crudService;

    public EditMessageText handleCallbackText(Update update) {
        String clickValue = update.getCallbackQuery().getData();
        User from = update.getCallbackQuery().getFrom();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        Subscriber user = crudService.toMap(Subscriber.class, from);
        EditMessageText msg = null;
        if (BotButton.verification.contains(clickValue)) {
            msg = workWithVerification(user, clickValue, messageId);
        }
        return msg;
    }


    private EditMessageText workWithVerification(Subscriber user, String clickValue, Integer messageId) {
        if (BotButton.VERIFICATION_FIND.getValue().equals(clickValue)) {
            return BotState.VERIFICATION_FIND.sendMessage(user, Collections.emptyList(), messageId);
        } else if (BotButton.VERIFICATION_SHOW.getValue().equals(clickValue)) {
            return BotState.VERIFICATION_SHOW.sendMessage(user, Collections.emptyList(), messageId);
        } else if (BotButton.VERIFICATION_UPDATE.getValue().equals(clickValue)) {
            return BotState.VERIFICATION_UPDATE.sendMessage(user, Collections.emptyList(), messageId);
        }
        return BotState.EMAIL_NOTIFICATION.sendMessage(user, Collections.emptyList(), messageId);
    }
}
