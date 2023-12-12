package com.denver7074.bot.service;

import com.denver7074.bot.configuration.BotConfig;
import com.denver7074.bot.service.messageservice.CallbackQueryService;
import com.denver7074.bot.service.messageservice.TextCommand;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TelegramBot extends TelegramLongPollingBot {

    BotConfig botConfig;
    TextCommand textCommand;
    CallbackQueryService callbackQueryService;

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
            }
        } if (update.hasCallbackQuery()) {
            execute(callbackQueryService.handleCallbackText(update));
        }
    }
}
