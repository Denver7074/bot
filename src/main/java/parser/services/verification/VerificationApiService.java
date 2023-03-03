package parser.services.verification;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import parser.model.Verification;
import parser.model.enums.BotState;
import parser.utils.TextUtil;
import parser.services.cache.UserVerificationNowCache;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class VerificationApiService {
    UserVerificationNowCache userVerificationNowCache;

    public String find(String command,BotState botState,Long userId) {
        String[] text = TextUtil.commandText(command);
        String answer = BotState.NOT_FOUND_VERIFICATION.getValue();
            List<Verification> list = api(text[0],text[1]);
        if (!list.isEmpty()) {
                if (botState.equals(BotState.HISTORY_VERIFICATION)) {
                    answer = TextUtil.stringUtil(list);
                }
                if (botState.equals(BotState.NOW_VERIFICATION)) {
                    answer = list.get(0).toString();
                }
                userVerificationNowCache.saveUserVerificationNow(userId,list.get(0));
            }
        return answer;
    }
    @SneakyThrows
    public List<Verification> api(String mitNumber,String number){
        String POST_API_URI ="https://fgis.gost.ru/fundmetrology/cm/xcdb/vri/select?fq=mi.mitnumber:"+ mitNumber + "&fq=mi.number:"+ number + "&q=*&fl=vri_id,org_title,mi.mitnumber,mi.modification,mi.number,verification_date,valid_date,applicability&sort=verification_date+desc,org_title+asc&rows=15&start=0";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .header("accept", "application/json")
                .uri(URI.create(POST_API_URI))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper mapper = objectMapper();
        JsonNode json = mapper.readTree(response.body());
        JsonNode docsArray = json.get("response").get("docs");
        CollectionLikeType postCollection = mapper.getTypeFactory().constructCollectionLikeType(ArrayList.class, Verification.class);
        return mapper.convertValue(docsArray, postCollection);
    }
    private ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
        mapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);
        return mapper;
    }


}
