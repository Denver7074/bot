package com.denver7074.bot.service;

import com.denver7074.bot.model.Email;
import com.denver7074.bot.model.Equipment;
import com.denver7074.bot.service.excel.ExcelServiceWrite;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MailService {

    final CrudService crudService;
    final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    String EMAIL_FROM;


    public void emailNotification(Map<Long, List<Equipment>> equipmentMap, String message) {
       for (Map.Entry<Long, List<Equipment>> eq : equipmentMap.entrySet()) {
           List<String> emailTo = crudService.find(Email.class, Collections.singletonMap(Email.Fields.userId, eq.getKey()), null)
                   .stream()
                   .map(Email::getEmail)
                   .toList();
           if (CollectionUtils.isEmpty(emailTo)) continue;
           mimeMessage(emailTo, message, eq.getValue());
       }
    }

    //формат файла не работает
    @SneakyThrows
    private void mimeMessage(List<String> emailTo, String text, List<Equipment> equipment) {
        try (InputStream inputStream = ExcelServiceWrite.writeDataToExcel(equipment);) {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true);

            String[] array =  emailTo.toArray(new String[0]);
            message.setFrom(EMAIL_FROM);
            message.setTo(array);
            message.setSubject("На поверку");
            message.setText(text);

            message.addAttachment("Поверка.xlsx", new InputStreamSource() {
                @Override
                public InputStream getInputStream() {
                    return inputStream;
                }
            }, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            mailSender.send(mimeMessage);
        }
    }
}