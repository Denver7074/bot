package com.denver7074.bot.service;

import com.denver7074.bot.configuration.BotConfig;
import com.denver7074.bot.model.BotButton;
import com.denver7074.bot.service.messageservice.CallbackQueryService;
import com.denver7074.bot.service.messageservice.FileMessageService;
import com.denver7074.bot.service.messageservice.TextCommand;
import com.denver7074.bot.utils.RedisCash;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;

import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TelegramBot extends TelegramLongPollingBot {

    RedisCash redisCash;
    BotConfig botConfig;
    TextCommand textCommand;
    CallbackQueryService callbackQueryService;
    FileMessageService fileMessageService;

    @SneakyThrows
    public void telegramBot() {
        List<BotCommand> botCommandList = List.of(
                new BotCommand("/start", "Старт"),
                new BotCommand("/help", "Расскажи о себе"),
                new BotCommand("/profile", "Мой профиль"),
                new BotCommand("/verification", "Поверка СИ"));
        execute(new SetMyCommands(botCommandList, new BotCommandScopeDefault(), null));
    }
    @Override
    public String getBotUsername() {
        return botConfig.getName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            if (update.getMessage().hasText()) {
                execute(textCommand.handleUpdateText(update));
            } else if (update.getMessage().hasDocument()) {
                execute(fileMessageService.handleUpdateText(update));
            }
        } else if (update.hasCallbackQuery()) {
            if (BotButton.showAndUpdate.contains(update.getCallbackQuery().getData())) execute(callbackQueryService.getFile(update));
            else execute(callbackQueryService.handleCallbackText(update));
        }
    }
}
