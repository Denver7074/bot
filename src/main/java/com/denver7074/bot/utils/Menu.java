package com.denver7074.bot.utils;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;

import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Menu {

    @SneakyThrows
    public SetMyCommands menu() {
        return new SetMyCommands(List.of(
                new BotCommand("/start", "Начинаем"),
                new BotCommand("/help", "Расскажи о себе"),
                new BotCommand("/profile", "Мой профиль"),
                new BotCommand("/verification", "Поверка СИ")),
                new BotCommandScopeDefault(),
                null);
    }
}
