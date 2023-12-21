package com.denver7074.bot.service.messageservice;

import com.denver7074.bot.model.*;
import com.denver7074.bot.service.CrudService;
import com.denver7074.bot.service.excel.ExcelRequest;
import com.denver7074.bot.service.excel.ExcelServiceWrite;
import com.denver7074.bot.utils.RedisCash;
import com.denver7074.bot.utils.errors.ToThrow;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;
import java.util.List;

import static com.denver7074.bot.model.BotButton.*;
import static com.denver7074.bot.model.BotState.VERIFICATION_SAVE;
import static com.denver7074.bot.utils.Constants.USER_STATE;
import static com.denver7074.bot.utils.errors.Errors.E005;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.ObjectUtils.isEmpty;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CallbackQueryService {

    RedisCash redisCash;
    CrudService crudService;

    public EditMessageText handleCallbackText(Update update) {
        String clickValue = update.getCallbackQuery().getData();
        BotButton botButton = verification.get(clickValue);
        if(isEmpty(botButton)) botButton = SAVE_VERIFICATION;
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        Subscriber user = redisCash.find(USER_STATE, update.getCallbackQuery().getFrom().getId(), Subscriber.class);
        EditMessageText editMsg = null;
        switch (botButton) {
            case SAVE_VERIFICATION -> {
                redisCash.save(user, null);
                editMsg = VERIFICATION_SAVE.sendMessage(user, emptyList(), messageId);
            }
            case EMAIL_NOTIFICATION -> editMsg = BotState.EMAIL_NOTIFICATION.sendMessage(user, crudService.find(user), messageId);
            default -> {
                Email emailForDelete = crudService.find(Email.class, Collections.singletonMap(Email.Fields.email, clickValue), null).stream().findFirst().orElse(null);
                crudService.delete(emailForDelete, Email.class);
                editMsg = BotState.EMAIL_NOTIFICATION.sendMessage(user, crudService.find(user), messageId);
            }
        }
        redisCash.save(user);
        return editMsg;
    }

    @ToThrow
    public SendDocument getFile(Update update) {
        Subscriber user = redisCash.find(USER_STATE, update.getCallbackQuery().getFrom().getId(), Subscriber.class);
        BotButton botButton = showAndUpdate.get(update.getCallbackQuery().getData());
        SendDocument sendDoc = null;
        switch (botButton) {
            case VERIFICATION_SHOW -> {
                List<Equipment> equipment = find(user);
                sendDoc = BotState.VERIFICATION_SHOW.sendMessage(user, ExcelServiceWrite.writeDataToExcel(equipment));
            }
            case VERIFICATION_UPDATE -> {
                List<ExcelRequest> excelRequests = find(user).stream().map(e -> crudService.toMap(ExcelRequest.class, e)).toList();
                sendDoc = BotState.VERIFICATION_UPDATE.sendMessage(user, ExcelServiceWrite.writeDataToExcel(excelRequests));
            }
            default -> {
                redisCash.save(user, null);
                sendDoc = BotState.VERIFICATION_FIND.sendMessage(user, ExcelServiceWrite.writeDataToExcel(emptyList()));
            }
        }
        redisCash.save(user);
        return sendDoc;
    }


    private List<Equipment> find(Subscriber user) {
        List<Equipment> equipment = crudService.find(Equipment.class, Collections.singletonMap(Equipment.Fields.userId, user.getId()), null);
        E005.thr(isEmpty(equipment), user.getId(), verification.keySet());
        return equipment;
    }

}
