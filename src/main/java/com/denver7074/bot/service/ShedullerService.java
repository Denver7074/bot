package com.denver7074.bot.service;

import com.denver7074.bot.model.BotState;
import com.denver7074.bot.model.Equipment;
import com.denver7074.bot.service.excel.ExcelServiceWrite;
import com.denver7074.bot.service.response.SendMsg;
import com.denver7074.bot.service.verification.VerificationService;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.emptyMap;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ShedullerService {

    CrudService crudService;
    TelegramBot telegramBot;
    MailService mailService;
    VerificationService verificationService;

    @SneakyThrows
//    @PostConstruct
    @Scheduled(cron = "${cron.finish.verification}")
    public void notificationFinishVerification() {
        Map<Long, List<Equipment>> equipmentMap = crudService
                .find(Equipment.class, emptyMap(), Pair.of(Equipment.Fields.validDate, LocalDate.now().plusDays(30)))
                .stream()
                .collect(Collectors.groupingBy(Equipment::getUserId));
        for (Long key : equipmentMap.keySet()) {
            List<Equipment> eq = equipmentMap.get(key);
            for (int i = 0; i < eq.size(); i++) {
                Equipment equipment = eq.get(i);
                Equipment lastVerification = verificationService.findLastVerification(key, equipment.getMitNumber(), equipment.getNumber());
                if (lastVerification.getValidDate().isAfter(equipment.getValidDate())) {
                    crudService.update(key, lastVerification, equipment.getId(), Equipment.class);
                    eq.remove(i);
                }
            }
            if (isEmpty(eq)) equipmentMap.remove(key);
            else equipmentMap.put(key, eq);
        }
        if (CollectionUtils.isEmpty(equipmentMap)) return;
        String message = BotState.VERIFICATION_FINISH.getMessage();

        List<SendDocument> sendDocs = new ArrayList<>(equipmentMap.size());
        for (Map.Entry<Long, List<Equipment>> id : equipmentMap.entrySet()) {
            sendDocs.add(
                    new SendMsg(message, id.getKey(), ExcelServiceWrite.writeDataToExcel(id.getValue())).getDocument()
            );
        }
        mailService.emailNotification(equipmentMap, message);
        for (SendDocument sendDoc : sendDocs) {
            telegramBot.sendDoc(sendDoc);
        }
    }

}
