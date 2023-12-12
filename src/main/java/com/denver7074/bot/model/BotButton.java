package com.denver7074.bot.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.EnumSet;
import java.util.List;

@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum BotButton {

    VERIFICATION_SHOW("\uD83D\uDDD3 Просмотр списка CИ"),
    VERIFICATION_FIND("\uD83D\uDD0E Поиск и добавление СИ "),
    EMAIL_NOTIFICATION("\uD83D\uDCE8 Управление e-mail оповещением"),
    VERIFICATION_UPDATE("Изменить название СИ"),
    //verification_find
    AUTO_FIND("⚙️Автоматический поиск с помощью шаблона"),
    //поставка на контроль СИ
    CONTROL_MEASURING_INSTRUMENT("\uD83D\uDCCC Поставить на контроль СИ"),
    NOT_CONTROL_MEASURING_INSTRUMENT("\uD83D\uDDD1 Снять с контроля СИ"),
    //подключение оповещения
    UNPLUG_EMAIL("\uD83D\uDCEA Отключить оповещение"),
    PLUG_EMAIL("\uD83D\uDCEB Подключить оповещение"),
    //редактирование
    DELETE_AUTO("⚙️Скачайте "),
    IMPORT_TO_EXCEL("\uD83D\uDCDA Импорт списка в excel");

    String value;

    public static final List<String> verification =
            List.of(VERIFICATION_FIND.getValue(), VERIFICATION_SHOW.getValue(), EMAIL_NOTIFICATION.getValue(),VERIFICATION_UPDATE.getValue());




}
