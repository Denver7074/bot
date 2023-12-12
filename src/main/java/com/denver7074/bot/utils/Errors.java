package com.denver7074.bot.utils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;

@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum Errors {

    E001(500, "Сущность %s по индивидуальному идентификатору %d не найдена"),
    E002(500, "Отсутствуют обязательные параметры: %s");

    int code;
    String description;

    public CustomException thr(Object... args) {
        return new CustomException(this, String.format(this.description, args));
    }

    @Getter
    @AllArgsConstructor
    @RequiredArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class CustomException extends RuntimeException {
        final Errors error;
        String message;

        public String getMsg(){
            return StringUtils.defaultString(message, error.description);
        }

        public String getMessage() {
            return getMsg();
        }

    }

}
