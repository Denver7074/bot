package com.denver7074.bot.service.messageservice;

import com.denver7074.bot.model.*;
import com.denver7074.bot.service.CrudService;
import com.denver7074.bot.service.excel.ExcelRequest;
import com.denver7074.bot.service.excel.ExcelServiceWrite;
import com.denver7074.bot.utils.RedisCash;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.*;

import static com.denver7074.bot.model.BotButton.*;
import static com.denver7074.bot.model.BotButton.VERIFICATION_UPDATE;
import static com.denver7074.bot.model.BotState.VERIFICATION_SAVE;
import static com.denver7074.bot.utils.Constants.USER_STATE;
import static java.util.Collections.emptyList;

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
        if (verification.contains(clickValue) || clickValue.equals(SAVE_VERIFICATION.getValue())) {
            msg = workWithVerification(user, clickValue, messageId);
        }
        return msg;
    }

    private EditMessageText workWithVerification(Subscriber user, String clickValue, Integer messageId) {
        EditMessageText editMsg = null;
        if(SAVE_VERIFICATION.getValue().equals(clickValue)) {
            redisCash.save(user, null);
            editMsg = VERIFICATION_SAVE.sendMessage(user, emptyList(), messageId);
        } else if(EMAIL_NOTIFICATION.getValue().equals(clickValue)){
            editMsg = BotState.EMAIL_NOTIFICATION.sendMessage(user, crudService.find(user), messageId);
        } else {
            Email emailForDelete = crudService.find(Email.class, Collections.singletonMap(Email.Fields.email, clickValue)).stream().findFirst().orElse(null);
            crudService.delete(emailForDelete, Email.class);
            return BotState.EMAIL_NOTIFICATION.sendMessage(user, crudService.find(user), messageId);
        }
        redisCash.save(user);
        return editMsg;
    }

    public SendDocument getFile(Update update) {
        Subscriber user = redisCash.find(USER_STATE, update.getCallbackQuery().getFrom().getId(), Subscriber.class);
        String click = update.getCallbackQuery().getData();
        SendDocument sendDoc = null;
        if (VERIFICATION_SHOW.getValue().equals(click)) {
            List<Equipment> equipment = crudService.find(Equipment.class, Collections.singletonMap(Equipment.Fields.userId, user.getId()));
            sendDoc = BotState.VERIFICATION_SHOW.sendMessage(user, ExcelServiceWrite.writeDataToExcel(equipment));
        } else if (VERIFICATION_UPDATE.getValue().equals(click)) {
            List<ExcelRequest> excelRequests = crudService.find(Equipment.class, Collections.singletonMap(Equipment.Fields.userId, user.getId()))
                    .stream().map(e -> crudService.toMap(ExcelRequest.class, e)).toList();
            sendDoc = BotState.VERIFICATION_UPDATE.sendMessage(user, ExcelServiceWrite.writeDataToExcel(excelRequests));
        } else {
            redisCash.save(user, null);
            sendDoc = BotState.VERIFICATION_FIND.sendMessage(user, ExcelServiceWrite.writeDataToExcel(emptyList()));
        }
        redisCash.save(user);
        return sendDoc;
    }

}
