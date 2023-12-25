package com.denver7074.bot.service;

import com.denver7074.bot.configuration.BotConfig;
import com.denver7074.bot.model.Email;
import com.denver7074.bot.model.Equipment;
import com.denver7074.bot.service.excel.ExcelServiceWrite;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.denver7074.bot.utils.Constants.FORMAT_FILE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MailService {

    BotConfig botConfig;
    CrudService crudService;
    JavaMailSender mailSender;


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
        try (InputStream inputStream = ExcelServiceWrite.writeDataToExcel(equipment)) {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true);

            String[] array =  emailTo.toArray(new String[0]);
            message.setFrom(botConfig.getUserName());
            message.setTo(array);
            message.setSubject("На поверку");
            message.setText(text);

            message.addAttachment("Поверка.xlsx", new InputStreamSource() {
                @Override
                public InputStream getInputStream() {
                    return inputStream;
                }
            }, FORMAT_FILE);

            mailSender.send(mimeMessage);
        }
    }
}
