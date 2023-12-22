package com.denver7074.bot.service;

import com.denver7074.bot.configuration.BotConfig;
import com.denver7074.bot.model.BotButton;
import com.denver7074.bot.service.messageservice.CallbackQueryService;
import com.denver7074.bot.service.messageservice.FileMessageService;
import com.denver7074.bot.service.messageservice.TextCommand;
import com.denver7074.bot.service.messageservice.invoice.InvoiceService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;

import java.io.Serializable;
import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TelegramBot extends TelegramLongPollingBot {

    BotConfig botConfig;
    TextCommand textCommand;
    InvoiceService invoiceService;
    FileMessageService fileMessageService;
    CallbackQueryService callbackQueryService;

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
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            if (update.getMessage().hasText()) {
                send(textCommand.handleUpdateText(update));
            } else if (update.getMessage().hasDocument()) {
                sendDoc(fileMessageService.handleUpdateText(update));
            }
        } else if (update.hasCallbackQuery()) {
            if (BotButton.showAndUpdate.containsKey(update.getCallbackQuery().getData())) sendDoc(callbackQueryService.getFile(update));
            else send(callbackQueryService.handleCallbackText(update));
        }
    }

    @SneakyThrows
    public void sendDoc(SendDocument send) {
            execute(send);
    }

    @SneakyThrows
    public <T extends Serializable, Method extends BotApiMethod<T>> void send(Method method) {
        execute(method);
    }
}
