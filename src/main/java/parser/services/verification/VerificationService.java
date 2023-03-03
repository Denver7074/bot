package parser.services.verification;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import parser.model.Constant;
import parser.model.User;
import parser.model.Verification;
import parser.repositories.VerificationRep;
import parser.services.UserService;



import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class VerificationService {

    VerificationApiService verificationApiService;

    VerificationRep verificationRep;

    UserService userService;

    public void saveVerification(Long userId, Verification verification){
        verification.setHref(Constant.HREF + verification.getIdVerification());
        verification.setWritingAboutVerification(verification.getValidDate().minusDays(userService.findUserById(userId).getDayMailing()));
        verification.setUser(userService.findUserById(userId));
        userService.updateCountSi(userId,1);
        verificationRep.save(verification);
        log.info("Save new verification. userId: {}, verification: {}",userId,verification);
    }

    public Verification findInstrument(String mitNumber, String number,Long userId){
        return verificationRep.findByUserAndMitNumberAndNumber(userId, mitNumber, number);
    }

    public List<Verification> findAllInstrument(Long userId){
        return verificationRep.findAllInstrument(userId);
    }

    public void updateVerification(User user, Verification v){
        verificationRep.updateVerification(v.getValidDate().minusDays(user.getDayMailing()),v.getIdVerification(),v.getValidDate(),v.getVerificationDate(),v.getApplicability(),v.getMitNumber(),Constant.HREF + v.getIdVerification(),v.getOrgTitle(),v.getNumber());
        log.info("Update verification: userId{}, verification: {}",user.getUserId(),v);
    }
    public void updateDateBefore(Long userId, int day){
        userService.updateDayMailing(userId,day);
        if (!findAllInstrument(userId).isEmpty()) {
            for (Verification v : findAllInstrument(userId)) {
                verificationRep.updateDateBefore(v.getIdVerification(), v.getValidDate().minusDays(day));
            }
        }
    }

    public void deleteVerification(Verification v, Long userId){
        verificationRep.deleteByIdVerification(v.getIdVerification());
        userService.updateCountSi(userId,-1);
        log.info("Delete verification: userId{}, verification: {}", userId,v);
    }

    public Map<User, List<Verification>> findBeforeFinishVerification(LocalDate date){
        return verificationRep.findByWritingAboutVerificationOOrValidDate(date).stream()
                .collect(Collectors.groupingBy(Verification::getUser));
    }

    public Map<User, List<Verification>> autoUpdateVerification(LocalDate date){
        for (Verification v : verificationRep.findFinishValidDate(date)) {
            updateVerification(v.getUser(),verificationApiService.api(v.getMitNumber(),v.getNumber()).get(0));
        }
        return verificationRep.findActualUpdate(date).stream()
                .collect(Collectors.groupingBy(Verification::getUser));
    }

    public void updateMiType(String idVerification, String miType){
        verificationRep.updateMiType(idVerification,miType);
        log.info("Update miType: idVerification{}, miType: {}",idVerification,miType);
    }
}
