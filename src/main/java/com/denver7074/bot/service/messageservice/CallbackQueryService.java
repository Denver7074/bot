package com.denver7074.bot.service.messageservice;

import ch.qos.logback.core.joran.conditional.IfAction;
import com.denver7074.bot.model.*;
import com.denver7074.bot.service.CrudService;
import com.denver7074.bot.service.response.SendMsg;
import com.denver7074.bot.utils.Constants;
import com.denver7074.bot.utils.RedisCash;
import com.denver7074.bot.utils.Utils;
import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodSerializable;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.denver7074.bot.model.BotButton.*;
import static com.denver7074.bot.model.BotState.VERIFICATION_SAVE;
import static com.denver7074.bot.utils.Constants.NO_EMAIL;
import static com.denver7074.bot.utils.Constants.USER_STATE;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.ObjectUtils.isEmpty;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CallbackQueryService {

    RedisCash redisCash;
    CrudService crudService;

    public EditMessageText handleCallbackText(Update update) {
        String clickValue = update.getCallbackQuery().getData();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        Subscriber user = redisCash.find(USER_STATE, update.getCallbackQuery().getFrom().getId(), Subscriber.class);
        EditMessageText msg = null;
        if (user.getBotState().equals(BotState.EMAIL_NOTIFICATION)) {
            msg = deleteEmail(user,clickValue, messageId);
        }
        if (verification.contains(clickValue) || plugEmail.contains(clickValue)) {
            msg = workWithVerification(user, clickValue, messageId);
        }
        return msg;
    }

    private EditMessageText deleteEmail(Subscriber user, String email, Integer messageId) {
        Email emailForDelete = crudService.find(Email.class, Collections.singletonMap(Email.Fields.email, email)).stream().findFirst().orElse(null);
        crudService.delete(emailForDelete, Email.class);
        return BotState.UNPLUG_EMAIL.sendMessage(user, crudService.find(user), messageId);
    }


    private EditMessageText workWithVerification(Subscriber user, String clickValue, Integer messageId) {
        EditMessageText editMsg = null;
        if (VERIFICATION_FIND.getValue().equals(clickValue)) {
            editMsg = BotState.VERIFICATION_FIND.sendMessage(user, emptyList(), messageId);
        } if(SAVE_VERIFICATION.getValue().equals(clickValue)) {
            redisCash.save(user, null);
            editMsg = new SendMsg(VERIFICATION_SAVE.getMessage(), user.getId(), emptyList(), messageId).getEditMsg();
        }
        // это нужно убрать так как будем выгружать excel файл
        else if (VERIFICATION_SHOW.getValue().equals(clickValue)) {
            editMsg = BotState.VERIFICATION_SHOW.sendMessage(user, emptyList(), messageId);
        }
        // тоже самое, работа с excel файлом
        else if (VERIFICATION_UPDATE.getValue().equals(clickValue)) {
            editMsg = BotState.VERIFICATION_UPDATE.sendMessage(user, emptyList(), messageId);
        } else if(EMAIL_NOTIFICATION.getValue().equals(clickValue)){
            editMsg = BotState.EMAIL_NOTIFICATION.sendMessage(user, crudService.find(user), messageId);
        }
        redisCash.save(user);
        return editMsg;
    }
}
