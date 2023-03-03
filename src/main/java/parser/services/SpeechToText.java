package parser.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import parser.model.Constant;
import parser.services.file.ExportService;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class SpeechToText {
    ExportService exportService;
    @SneakyThrows
    public String postRequest(String fileId) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(Constant.POST_URL_SPEECH))
                .header("Authorization", Constant.API_KEY_YANDEX_CLOUD)
                .POST(HttpRequest.BodyPublishers.ofByteArray(exportService.loadFile(fileId)))
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> post = client.send(request, HttpResponse.BodyHandlers.ofString());
        return post.body().substring(post.body().indexOf(":") + 2,post.body().length()-2);
    }

    @SneakyThrows
    public String commandForBot(String fileId){
        String firstText = postRequest(fileId).toLowerCase();
        String command = postRequest(fileId).toLowerCase();
        String[] commands = {"старт","помо","аудит"};
        String[] commandsBot = {"/start","/help","/audit"};
        for (int i = 0; i < commands.length; i++){
            String secondText = firstText.replaceAll(commands[i],"");
            if (firstText.replaceAll(secondText,"").equals(commands[i])){
                command = commandsBot[i];
            }
        }
        return command;
    }
}
