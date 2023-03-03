package parser.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import parser.model.User;
import parser.model.Verification;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class TextUtil {

    public static String[] commandText(String command){
        command = command.replaceAll(" ","");
        String[] text = {command.substring(0,command.indexOf("-")+3),command.substring(command.indexOf("-")+3)};
        return text;
    }

    public static String[] commandPlugEmail(String command){
        return command.replaceAll(" ", "").split(",");
    }

    public static String stringUtil(List<?> list){
        return StringUtils.removeEnd(
                StringUtils.removeStart(list.toString(), "["), "]")
                .replaceAll(", ", "");
    }

    public static List<String> getNameAndAgeStrings(List<User> users) {
        List<String> result = new ArrayList<>();
        for (User u : users) {
            result.add("Name: " + u.getName() + ", Age: " + u.getUserId());
        }
        return result;
    }




}
