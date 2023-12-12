package com.denver7074.bot.model;

import com.denver7074.bot.service.response.SendMsg;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import java.util.EnumSet;
import java.util.List;

@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum BotState {
    // старт
    SHOW_START_MENU("Привет, %s! \n Выберете интересующую вас команду из Меню \uD83D\uDCCB " +
            "или нажмите /help \uD83C\uDD98 для ознакомления со списком команд"),
    //Главное меню
    WORK_WITH_VERIFICATION("В данном разделе 'Поверка СИ' бот " +
            "позволит вам организовать следующие:"),
    SHOW_HELP_MENU("Бот поможет вам в поиске поверке СИ /verification \uD83D\uDD0D\n\n"),
    ABOUT_ME("Мой профиль:\n"),
    DEFAULT("Я тебя не понимаю"),

    VERIFICATION_FIND("Введите номер ГРСИ и заводской номер через пробел или скачайте шаблон, " +
            "заполните обязательные поля и загрузите обратным сообщением\n Пример ввода: 51124-12 191"),
    VERIFICATION_SHOW("Просмотр всех СИ закрепленнх за пользователем:"),
    //вот здесь нужно выгрузить все средства измерения в excel и на основе этой таблицы внести правки
    VERIFICATION_UPDATE("Вы можете изменить только название оборудование, если оно было загружено из ФГИС Аршин некорректно.\n" +
                        "Скачайте шаблон excel, введите корректное название СИ и ответным сообщением отправьте шаблон с исправленными названиями."),
    EMAIL_NOTIFICATION("Что желаете?");

    public static final EnumSet<BotState> botStates = EnumSet.copyOf(
            List.of(WORK_WITH_VERIFICATION)
    );

    String message;

    public SendMessage sendMessage(Subscriber user, List<String> buttons, Object... args) {
        user.setBotState(this);
        return new SendMsg(String.format(this.message, args), this, user.getId(), buttons).getMsg();
    }

    public SendMessage sendMessage(Subscriber user, List<String> buttons) {
        user.setBotState(this);
        return new SendMsg(this.message, this, user.getId(), buttons).getMsg();
    }

    public EditMessageText sendMessage(Subscriber user, List<String> buttons, Integer messageId, Object... args) {
        user.setBotState(this);
        return new SendMsg(String.format(this.message, args), this, user.getId(), buttons, messageId).getEditMsg();
    }

    public EditMessageText sendMessage(Subscriber user, List<String> buttons, Integer messageId) {
        user.setBotState(this);
        return new SendMsg(this.message, this, user.getId(), buttons, messageId).getEditMsg();
    }


}
