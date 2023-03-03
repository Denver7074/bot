package parser.messageservice.callback;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import parser.model.Mailing;
import parser.model.User;
import parser.model.Verification;
import parser.model.enums.BotState;
import parser.model.enums.ButtonProfile;
import parser.services.MailService;
import parser.services.UserService;
import parser.services.cache.PageUserCache;
import parser.services.cache.UserVerificationNowCache;
import parser.services.verification.VerificationService;
import parser.utils.TextUtil;

import java.util.List;

import static parser.model.enums.ButtonVerification.*;


@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class VerificationCallback {
    UserService userService;
    MailService mailService;

    PageUserCache pageUserCache;
    VerificationService verificationService;
    UserVerificationNowCache userVerificationNowCache;


    public String verificationClick(String callBackData, Long userId) {
        return switch (userService.findUserById(userId).getBotState()) {
            case WORK_WITH_VERIFICATION -> workVerification(userId, callBackData);
            case VERIFICATION_FIND -> findVerification(userId, callBackData);
            case FIND_MANUAL -> manualFind(userId, callBackData);
            case HISTORY_VERIFICATION, NOW_VERIFICATION, SHOW_MY_INSTRUMENT, DELETE_MANUAL ->
                    controlClick(userId, callBackData);
            case VERIFICATION_SHOW -> showInstrument(userId, callBackData);
            case VERIFICATION_UPDATE -> update(userId, callBackData);
            case DELETE -> delete(userId, callBackData);
            case EMAIL_NOTIFICATION -> emailNotification(userId, callBackData);
            case ABOUT_ME -> profileManagement(userId, callBackData);
            case DELETE_ME -> softDeleteUser(userId, callBackData);
            case SHOW_ME_USERS -> flippingPage(userId,callBackData);
            default -> "";
        };
    }

    //вошли в меню поверке
    private String workVerification(Long userId, String callBackData) {
        String text = BotState.VERIFICATION_FIND.getValue();
        BotState botState = BotState.VERIFICATION_FIND;
        if (callBackData.equals(VERIFICATION_SHOW.getValue())) {
            botState = BotState.VERIFICATION_SHOW;
            text = BotState.VERIFICATION_SHOW.getValue();
        }
        if (callBackData.equals(VERIFICATION_UPDATE.getValue())) {
            botState = BotState.VERIFICATION_UPDATE;
            text = BotState.VERIFICATION_UPDATE.getValue();
        }
        if (callBackData.equals(EMAIL_NOTIFICATION.getValue())) {
            botState = BotState.EMAIL_NOTIFICATION;
            text = BotState.EMAIL_NOTIFICATION.getValue();
        }
        userService.updateBotState(userId, botState);
        return text;
    }

    //нажали на поиск
    private String findVerification(Long userId, String callBackData) {
        String text = BotState.FIND_MANUAL.getValue();
        BotState botState = BotState.FIND_MANUAL;
        if (callBackData.equals(AUTO_FIND.getValue())) {
            botState = BotState.FIND_AUTO;
            text = BotState.FIND_AUTO.getValue();
        }
        userService.updateBotState(userId, botState);
        return text;
    }

    //ручной поиск
    private String manualFind(Long userId, String callBackData) {
        BotState botState = BotState.NOW_VERIFICATION;
        String text = BotState.NOW_VERIFICATION.getValue();
        if (callBackData.equals(HISTORY_VERIFICATION.getValue())) {
            botState = BotState.HISTORY_VERIFICATION;
            text = BotState.HISTORY_VERIFICATION.getValue();
        }
        userService.updateBotState(userId, botState);
        return text;
    }

    //поставить на контроль СИ
    private String controlClick(Long userId, String callBackData) {
        String text = BotState.CONTROL_MEASURING_INSTRUMENT.getValue() + userService.findUserById(userId).getDayMailing();
        BotState botState = BotState.CONTROL_MEASURING_INSTRUMENT;
        Verification userVerificationNow = userVerificationNowCache.getUserVerificationNow(userId);
        if (callBackData.equals(NOT_CONTROL_MEASURING_INSTRUMENT.getValue())) {
            text = BotState.NOT_CONTROL_MEASURING_INSTRUMENT.getValue();
            verificationService.deleteVerification(userVerificationNow,userId);
            botState = BotState.DELETE_VERIFICATION;
        } else {
            verificationService.saveVerification(userId, userVerificationNow);
        }
        userService.updateBotState(userId, botState);
        return text;
    }

    //просмотр записей
    private String showInstrument(Long userId, String callBackData) {
        String text = BotState.SHOW_MY_INSTRUMENT.getValue();
        BotState botState = BotState.SHOW_MY_INSTRUMENT;
        if (callBackData.equals(IMPORT_TO_EXCEL.getValue())) {
            text = BotState.IMPORT_TO_EXCEL.getValue();
            botState = BotState.IMPORT_TO_EXCEL;
        }
        userService.updateBotState(userId, botState);
        return text;
    }

    private String update(Long userId, String callBackData) {
        String text = BotState.UPDATE_AUTO.getValue();
        BotState botState = BotState.UPDATE_AUTO;
        if (callBackData.equals(DELETE.getValue())) {
            text = BotState.DELETE.getValue();
            botState = BotState.DELETE;
        }
        if (callBackData.equals(UPDATE_DAY_MAILING.getValue())) {
            text = BotState.UPDATE_DAY_MAILING.getValue() + userService.findUserById(userId).getDayMailing();
            botState = BotState.UPDATE_DAY_MAILING;
        }
        userService.updateBotState(userId, botState);
        return text;
    }

    public String delete(Long userId, String callBackData) {
        String text = BotState.DELETE_MANUAL.getValue();
        BotState botState = BotState.DELETE_MANUAL;
        if (callBackData.equals(DELETE_AUTO.getValue())) {
            text = BotState.DELETE_AUTO.getValue();
            botState = BotState.DELETE_AUTO;
        }
        userService.updateBotState(userId, botState);
        return text;
    }

    private String emailNotification(Long userId, String callBackData) {
        List<Mailing> mailings = mailService.findAllMailing(userId);
        String text = BotState.UNPLUG_EMAIL.getValue() + TextUtil.stringUtil(mailings);
        BotState botState = BotState.UNPLUG_EMAIL;
        if (callBackData.equals(PLUG_EMAIL.getValue())) {
            botState = BotState.PLUG_EMAIL;
            text = BotState.PLUG_EMAIL.getValue() + TextUtil.stringUtil(mailings);
        }
        userService.updateBotState(userId, botState);
        return text;
    }

    private String profileManagement(Long userId, String callBackData){
        List<User> byDeleted = userService.findByDeleted(0, 2);
        userService.countPage(2);
        String text =  byDeleted.toString();
        BotState botState = BotState.SHOW_ME_USERS;
        if (callBackData.equals(ButtonProfile.DELETE_ME.getValue())){
            botState = BotState.DELETE_ME;
            text = BotState.DELETE_ME.getValue();
        }
        userService.updateBotState(userId, botState);
        return text;
    }

    public String softDeleteUser(Long userId, String callBackData){
        if (callBackData.equals(ButtonProfile.YES.getValue())){
            userService.deleteSoft(userId);
            userService.updateBotState(userId,BotState.SOFT_DELETE_USER);
            return BotState.SOFT_DELETE_USER.getValue();
        }
        return null;
    }

    public String flippingPage(Long userId, String callBackData){
        if (callBackData.equals(ButtonProfile.NEXT_PAGE.getValue())){
            int page = pageUserCache.getNumberPage(userId) + 1;
            pageUserCache.saveNumberPage(userId,page);
            List<User> byDeleted = userService.findByDeleted(page, 2);
            return TextUtil.getNameAndAgeStrings(byDeleted).toString();
        }
            int page = pageUserCache.getNumberPage(userId) - 1;
            pageUserCache.saveNumberPage(userId,page);
            List<User> byDeleted = userService.findByDeleted(page, 2);
            return TextUtil.getNameAndAgeStrings(byDeleted).toString();
    }

}
