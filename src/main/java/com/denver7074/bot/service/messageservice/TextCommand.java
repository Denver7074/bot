package com.denver7074.bot.service.messageservice;

import com.denver7074.bot.model.BotButton;
import com.denver7074.bot.model.BotState;
import com.denver7074.bot.model.Subscriber;
import com.denver7074.bot.service.CrudService;
import com.denver7074.bot.service.verification.VerificationService;
import com.denver7074.bot.utils.Utils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import static com.denver7074.bot.model.BotState.SHOW_HELP_MENU;
import static com.denver7074.bot.model.BotState.SHOW_START_MENU;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.ObjectUtils.isEmpty;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TextCommand {

    CrudService crudService;
    VerificationService service;

    public SendMessage handleUpdateText(Update update) {
        String command = update.getMessage().getText();
        User from = update.getMessage().getFrom();
        Subscriber user = Utils.safeGet(() -> crudService.find(Subscriber.class, from.getId()));
        if (isEmpty(user)) {
            user = crudService.create(from, Subscriber.class);
        }
        SendMessage sendMsg = null;
        switch (command) {
            case "/start"-> sendMsg = SHOW_START_MENU.sendMessage(user, emptyList(), user.getFirstName());
            case "/help" -> sendMsg = SHOW_HELP_MENU.sendMessage(user, emptyList());
            case "/verification"-> sendMsg = BotState.WORK_WITH_VERIFICATION.sendMessage(user, BotButton.verification);
            case "/about me"-> sendMsg = BotState.ABOUT_ME.sendMessage(user, emptyList());
            default-> sendMsg = SendMessage
                    .builder()
                    .chatId(user.getId())
                    .text(service.findLastVerification(command).get(0).toString())
                    .build();
        };
        crudService.update(user, user.getId());
        return sendMsg;
    }




}
