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

    E001( "Прибор с даннми %s не найден. Проверьте введенные данные и повторите ввод или вернитесь в главное меню."),
    E002( "Отсутствуют обязательные параметры: %s"),
    E003("Такой прибор уже стоит на контроле у вас. Повторите поиск или выберите команду из меню.");


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
