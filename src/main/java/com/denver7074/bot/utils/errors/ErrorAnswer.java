package com.denver7074.bot.utils.errors;

import com.denver7074.bot.service.TelegramBot;
import com.denver7074.bot.service.response.SendMsg;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import static java.util.Collections.emptyList;

@Aspect
@Order(2)
@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ErrorAnswer {

    TelegramBot telegramBot;

    @AfterThrowing(pointcut = "@annotation(ToThrow)", throwing = "exception")
    public void handleException(Errors.CustomException exception) {
        telegramBot.error(new SendMsg(exception.getMsg(), exception.getUserId(), emptyList()).getMsg());
    }
}
