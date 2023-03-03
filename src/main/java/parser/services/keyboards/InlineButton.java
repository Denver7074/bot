package parser.services.keyboards;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import parser.model.Constant;
import parser.model.Verification;
import parser.model.enums.BotState;
import parser.model.enums.ButtonProfile;
import parser.model.enums.ButtonVerification;
import parser.services.UserService;
import parser.services.cache.PageUserCache;
import parser.services.cache.UserVerificationNowCache;
import parser.services.verification.VerificationService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class InlineButton {
    UserService userService;
    PageUserCache pageUserCache;
    VerificationService verificationService;

    UserVerificationNowCache userVerificationNowCache;

    private List<String> buttonName(Long userId) {
        List<String> listButton = new LinkedList<>();
        switch (userService.findUserById(userId).getBotState()) {
            case VERIFICATION_FIND ->
                    listButton.addAll(List.of(ButtonVerification.AUTO_FIND.getValue(), ButtonVerification.MANUAL_FIND.getValue()));
            case WORK_WITH_VERIFICATION -> {
                listButton.addAll(List.of(ButtonVerification.VERIFICATION_FIND.getValue(), ButtonVerification.EMAIL_NOTIFICATION.getValue()));
                if (userService.findUserById(userId).getCountSi() != 0) {
                    listButton.addAll(List.of(ButtonVerification.VERIFICATION_SHOW.getValue(),ButtonVerification.VERIFICATION_UPDATE.getValue()));
                }
            }
            case FIND_MANUAL ->
                    listButton.addAll(List.of(ButtonVerification.NOW_VERIFICATION.getValue(), ButtonVerification.HISTORY_VERIFICATION.getValue()));
            case HISTORY_VERIFICATION, NOW_VERIFICATION, SHOW_MY_INSTRUMENT, DELETE_MANUAL -> {
                Verification v = userVerificationNowCache.getUserVerificationNow(userId);
                if (verificationService.findInstrument(v.getMitNumber(), v.getNumber(), userId) == null) {
                    listButton.add(ButtonVerification.CONTROL_MEASURING_INSTRUMENT.getValue());
                } else {
                    listButton.add(ButtonVerification.NOT_CONTROL_MEASURING_INSTRUMENT.getValue());
                }
            }
            case VERIFICATION_UPDATE ->
                    listButton.addAll(List.of(ButtonVerification.UPDATE_AUTO.getValue(), ButtonVerification.UPDATE_DAY_MAILING.getValue(), ButtonVerification.DELETE.getValue()));
            case DELETE ->
                    listButton.addAll(List.of(ButtonVerification.DELETE_MANUAL.getValue(), ButtonVerification.DELETE_AUTO.getValue()));
                case VERIFICATION_SHOW -> listButton.addAll(List.of(ButtonVerification.SHOW_MY_INSTRUMENT.getValue(), ButtonVerification.IMPORT_TO_EXCEL.getValue()));

            case EMAIL_NOTIFICATION ->
                    listButton.addAll(List.of(ButtonVerification.UNPLUG_EMAIL.getValue(), ButtonVerification.PLUG_EMAIL.getValue()));
            case ABOUT_ME -> {
                if (String.valueOf(userId).equals(Constant.ADMIN_ID)) {
                    listButton.add(ButtonProfile.SHOW_ME_USERS.getValue());
                } else {
                    listButton.add(ButtonProfile.DELETE_ME.getValue());
                }
            }
            case DELETE_ME -> listButton.add(ButtonProfile.YES.getValue());
            case SHOW_ME_USERS -> {
                int numberPage = pageUserCache.getNumberPage(userId);
                String previous = (numberPage - 1) + " " + ButtonProfile.PREVIOUS_PAGE.getValue();
                String next = ButtonProfile.NEXT_PAGE.getValue() + " " + (numberPage + 1);
                if (pageUserCache.getNumberPage(userId) != 0){
                    listButton.add(previous);
                }
                if (pageUserCache.getNumberPage(userId) != pageUserCache.getCountPage() - 1){
                    listButton.add(next);
                }
            }
        }
        return listButton;
    }

    public List<List<InlineKeyboardButton>> getButton(Long userId) {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
            List<String> lisButtonName = buttonName(userId);
            for (String l : lisButtonName) {
                buttons.add(List.of(
                        InlineKeyboardButton.builder()
                                .text(l)
                                .callbackData(l)
                                .build()));
            }
            return buttons;
        }
}

