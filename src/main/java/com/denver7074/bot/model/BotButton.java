package com.denver7074.bot.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum BotButton {
    SAVE_VERIFICATION("Поставить на контроль"),
    VERIFICATION_SHOW("\uD83D\uDDD3 Просмотр списка CИ"),
    VERIFICATION_FIND("\uD83D\uDD0E Поиск и добавление СИ "),
    EMAIL_NOTIFICATION("\uD83D\uDCE8 Управление e-mail оповещением"),
    VERIFICATION_UPDATE("Изменить название СИ"),
    //verification_find
    AUTO_FIND("⚙️Автоматический поиск с помощью шаблона");

    String value;

    public static final Map<String, BotButton> verification = Map.of(
            VERIFICATION_FIND.getValue(), VERIFICATION_FIND,
            VERIFICATION_SHOW.getValue(), VERIFICATION_SHOW,
            EMAIL_NOTIFICATION.getValue(), EMAIL_NOTIFICATION,
            VERIFICATION_UPDATE.getValue(), VERIFICATION_UPDATE
    );

    public static final Map<String, BotButton> showAndUpdate = Map.of(
            VERIFICATION_SHOW.getValue(), VERIFICATION_SHOW,
            VERIFICATION_UPDATE.getValue(), VERIFICATION_UPDATE,
            VERIFICATION_FIND.getValue(), VERIFICATION_FIND
    );

}
