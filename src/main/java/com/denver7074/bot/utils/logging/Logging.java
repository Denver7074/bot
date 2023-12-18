package com.denver7074.bot.utils.logging;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

@Slf4j
@Aspect
@Component
@Order(1)
public class Logging {

    @SneakyThrows
    @Around("execution(* com.denver7074.bot.service.*.*(..)) || @annotation(ToLog)")
    public Object logging(ProceedingJoinPoint joinPoint) {
        String nameClass = joinPoint.getSignature().getDeclaringType().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        String startMessage = isNotEmpty(args)
                ? String.format("Start method %s in the class %s with arguments %s", methodName, nameClass, Arrays.asList(args))
                : String.format("Start method %s in the class %s", methodName, nameClass);
        log.info(startMessage);
        Object result = joinPoint.proceed();
        String finishMessage = isNotEmpty(result)
                ? String.format("Finished method %s in the class %s with results %s", methodName, nameClass, result)
                : String.format("Finished method %s in the class %s", methodName, nameClass);
        log.info(finishMessage);
        return result;
    }
}
