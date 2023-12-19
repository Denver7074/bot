package com.denver7074.bot.service.messageservice;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import static com.denver7074.bot.utils.Constants.USER_STATE;
import static com.denver7074.bot.utils.Utils.safeGet;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileMessageService {

    final RedisCash redisCash;
    final CrudService crudService;
    final ExcelServiceRead excelServiceRead;
    @Value("${service.file_info.uri}")
    private String FILE_INFO_URI;
    @Value("${service.file_storage.uri}")
    private String fileStorageUri;


    public SendDocument handleUpdateText(Update update) {
        Subscriber user = safeGet(() -> redisCash.find(USER_STATE, update.getMessage().getFrom().getId(), Subscriber.class));
        excelServiceRead.readExcel(loadFile(update), user);
        List<Equipment> equipment = crudService.find(Equipment.class, Collections.singletonMap(Equipment.Fields.userId, user.getId()), null);
        SendDocument sendDocument = BotState.VERIFICATION_SHOW.sendMessage(user, ExcelServiceWrite.writeDataToExcel(equipment));
        redisCash.save(user);
        return sendDocument;
    }

    @SneakyThrows
    private ByteArrayInputStream loadFile(Update update) {
        String fileId = update.getMessage().getDocument().getFileId();
        URL url = new URL(FILE_INFO_URI + fileId);
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        String getFile = br.readLine();
        JSONObject jsonObject = new JSONObject(getFile);
        JSONObject path = jsonObject.getJSONObject("result");
        String filePath = path.getString("file_path");
        try (InputStream is = new URL(fileStorageUri + filePath).openStream()) {
            br.close();
            return new ByteArrayInputStream(IOUtils.toByteArray(is));
        }
    }
}
