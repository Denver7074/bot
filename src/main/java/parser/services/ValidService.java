package parser.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import parser.model.User;



import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class ValidService {

    MailService mailService;

    public String isValidEmail(User user, String[] email){
        Map<User,List<String>> mapUnValid = new HashMap<>();
        String regex = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        for (String e : email) {
            Matcher matcher = pattern.matcher(e);
            if (matcher.matches()) {
                mailService.saveEmail(user,e);
            }
            else {
                mapUnValid.put(user,List.of(e));
            }
        }
        if (!mapUnValid.isEmpty()){
            return "Оповещение подключено для следующих e-mail адресов:\n" + mailService.findAllMailing(user.getUserId()).toString() + "\n" +
                    "Проверьте корректность ввода следующих e-mail адресов:\n" + mapUnValid.values();
        }
        return "Оповещение подключено для следующих e-mail адресов:\n" + mailService.findAllMailing(user.getUserId()).toString();
    }


    public String validInstrument(Map<Long,List<String>> map){
        return "text";
    }
}
