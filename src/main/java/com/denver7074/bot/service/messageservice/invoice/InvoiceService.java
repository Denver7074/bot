package com.denver7074.bot.service.messageservice.invoice;

import com.denver7074.bot.service.messageservice.FileMessageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvoiceService {

//    InvoiceRep invoiceRep;
    final FileMessageService fileMessageService;

    @Value("${yandex.kit.key}")
    String API_KEY_YANDEX_CLOUD;
    @Value("${yandex.kit.url}")
    String POST_URL_SPEECH;


    @SneakyThrows
    public String handleUpdateVoice(Update update) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(POST_URL_SPEECH))
                .header("Authorization", API_KEY_YANDEX_CLOUD)
                .POST(HttpRequest.BodyPublishers.ofByteArray(fileMessageService.loadFile(update)))
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> post = client.send(request, HttpResponse.BodyHandlers.ofString());
        return post.body().substring(post.body().indexOf(":") + 2,post.body().length()-2);
    }

//    public String handleUpdateVoiceFeign(Update update) {
//        String data = invoiceRep.getData(API_KEY_YANDEX_CLOUD, fileMessageService.loadFile(update));
//        return data;
//    }
}
