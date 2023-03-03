package parser.services.keyboards;

import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Menu {
    public static SetMyCommands menu() {
        List<BotCommand> botCommandList = new ArrayList<>();
        Collections.addAll(botCommandList,
                new BotCommand("/start", "Начинаем"),
                new BotCommand("/help", "Расскажи о себе"),
                new BotCommand("/profile", "Мой профиль"),
                new BotCommand("/verification", "Поверка СИ"),
                new BotCommand("/audit", "Внешний аудит документов СМ"),
                new BotCommand("/advertisement", "Предложения по рекламе и сотрудничеству"));
        return new SetMyCommands(botCommandList, new BotCommandScopeDefault(), null);
    }
}
