package com.denver7074.bot.service.messageservice;

import com.denver7074.bot.model.BotButton;
import com.denver7074.bot.model.BotState;
import com.denver7074.bot.model.Email;
import com.denver7074.bot.model.Subscriber;
import com.denver7074.bot.service.CrudService;
import com.denver7074.bot.service.verification.VerificationService;
import com.denver7074.bot.utils.Constants;
import com.denver7074.bot.utils.RedisCash;
import com.denver7074.bot.utils.Utils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.denver7074.bot.model.BotState.*;
import static com.denver7074.bot.utils.Constants.NO_EMAIL;
import static com.denver7074.bot.utils.Constants.USER_STATE;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.ObjectUtils.isEmpty;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TextCommand {

    RedisCash redisCash;
    CrudService crudService;
    VerificationService service;

    public SendMessage handleUpdateText(Update update) {
        Subscriber user = Utils.safeGet(() -> redisCash.find(USER_STATE, update.getMessage().getFrom().getId(), Subscriber.class));
        if (isNotEmpty(user) && BotState.botStates.contains(user.getBotState())) {
            return sendFromBotState(update, user);
        }
        return sendFromText(update);
    }

    private SendMessage sendFromText(Update update) {
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
            default -> DEFAULT.sendMessage(user, emptyList());
        };
        redisCash.save(user);
        return sendMsg;
    }

    private SendMessage sendFromBotState(Update update, Subscriber user) {
        String command = update.getMessage().getText();
        SendMessage sendMsg = null;
        switch (user.getBotState()) {
            case VERIFICATION_FIND, SAVE_VERIFICATION-> sendMsg = SAVE_VERIFICATION.sendMessage(user, List.of(BotButton.SAVE_VERIFICATION.getValue()), service.findLastVerification(user, command).toString());
            case PLUG_EMAIL -> {
                crudService.create(new Email().setEmail(command).setUserId(user.getId()));
                sendMsg = PLUG_EMAIL.sendMessage(user, emptyList(), crudService.find(user));
            }
            default -> sendMsg = DEFAULT.sendMessage(user, emptyList());
        };
        redisCash.save(user);
        return sendMsg;
    }

    private EditMessageText sendMessageSaveEmail(Update update, Subscriber user) {
        String command = update.getMessage().getText();
        EditMessageText editMsg = null;
        Integer messageId = update.getMessage().getMessageId();
        switch (user.getBotState()) {
            case PLUG_EMAIL -> {
                crudService.create(new Email().setEmail(command).setUserId(user.getId()));
                editMsg = PLUG_EMAIL.sendMessage(user, crudService.find(user), messageId, crudService.find(user));
            }
            default -> editMsg = DEFAULT.sendMessage(user, emptyList(), messageId, crudService.find(user));
        }
        redisCash.save(user);
        return editMsg;
    }



}
