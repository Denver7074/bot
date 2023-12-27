package com.denver7074.bot.service.messageservice;

import com.denver7074.bot.configuration.BotConfig;
import com.denver7074.bot.model.BotState;
import com.denver7074.bot.model.Equipment;
import com.denver7074.bot.model.Subscriber;
import com.denver7074.bot.service.CrudService;
import com.denver7074.bot.service.excel.ExcelServiceRead;
import com.denver7074.bot.service.excel.ExcelServiceWrite;
import com.denver7074.bot.utils.RedisCash;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import static com.denver7074.bot.utils.Constants.USER_STATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileMessageService {

    RedisCash redisCash;
    CrudService crudService;
    ExcelServiceRead excelServiceRead;
    BotConfig botConfig;

    public SendDocument handleUpdateText(Update update) {
        Subscriber user = redisCash.find(USER_STATE, update.getMessage().getFrom().getId(), Subscriber.class);
        excelServiceRead.readExcel(new ByteArrayInputStream(loadFile(update.getMessage().getDocument().getFileId())), user);
        List<Equipment> equipment = crudService.find(Equipment.class, Collections.singletonMap(Equipment.Fields.userId, user.getId()), null);
        SendDocument sendDocument = BotState.VERIFICATION_SHOW.sendMessage(user, ExcelServiceWrite.writeDataToExcel(equipment));
        redisCash.save(user);
        return sendDocument;
    }

    @SneakyThrows
    public byte[] loadFile(String fileId) {
        URL url = new URL(botConfig.getFileInfo() + fileId);
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        String getFile = br.readLine();
        JSONObject jsonObject = new JSONObject(getFile);
        JSONObject path = jsonObject.getJSONObject("result");
        String filePath = path.getString("file_path");
        return IOUtils.toByteArray(new URL(botConfig.getFileStore() + filePath).openStream());
    }
}
