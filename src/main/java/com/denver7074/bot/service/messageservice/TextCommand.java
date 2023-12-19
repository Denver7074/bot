package com.denver7074.bot.service.messageservice;

import com.denver7074.bot.model.BotButton;
import com.denver7074.bot.model.BotState;
import com.denver7074.bot.model.Email;
import com.denver7074.bot.model.Subscriber;
import com.denver7074.bot.service.CrudService;
import com.denver7074.bot.service.verification.VerificationService;
import com.denver7074.bot.utils.RedisCash;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;

import static com.denver7074.bot.model.BotState.*;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.ObjectUtils.isEmpty;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TextCommand {

    RedisCash redisCash;
    CrudService crudService;
    VerificationService service;

    public SendMessage handleUpdateText(Update update) {
        User from = update.getMessage().getFrom();
        String command = update.getMessage().getText();
        //подключу когда кеширование до конца сделаю
//        Subscriber user = Utils.safeGet(() -> redisCash.find(USER_STATE, update.getMessage().getFrom().getId(), Subscriber.class));
        Subscriber user = crudService.findNullable(Subscriber.class, update.getMessage().getFrom().getId());
        if (isEmpty(user)) user = crudService.create(from, Subscriber.class);
        return sendFromText(command, user);
    }

    private SendMessage sendFromText(String command, Subscriber user) {
        SendMessage sendMsg = null;
        switch (command) {
            case "/start"-> sendMsg = SHOW_START_MENU.sendMessage(user, emptyList(), user.getFirstName());
            case "/help" -> sendMsg = SHOW_HELP_MENU.sendMessage(user, emptyList());
            case "/verification"-> sendMsg = BotState.WORK_WITH_VERIFICATION.sendMessage(user, BotButton.verification);
            case "/profile"-> sendMsg = BotState.ABOUT_ME.sendMessage(user, emptyList(), user.toString());
            default -> sendMsg = sendFromBotState(command, user);
        };
        redisCash.save(user);
        return sendMsg;
    }

    private SendMessage sendFromBotState(String command, Subscriber user) {
        SendMessage sendMsg = null;
        switch (user.getBotState()) {
            case VERIFICATION_FIND, SAVE_VERIFICATION->  sendMsg = SAVE_VERIFICATION.sendMessage(user, List.of(BotButton.SAVE_VERIFICATION.getValue()), service.findLastVerification(user, command).toString());
            case EMAIL_NOTIFICATION ->  {
                crudService.create(new Email().setEmail(command).setUserId(user.getId()));
                sendMsg = EMAIL_NOTIFICATION.sendMessage(user, crudService.find(user));
            }
            default ->  sendMsg = DEFAULT.sendMessage(user, emptyList());
        }
        return sendMsg;
    }

}
