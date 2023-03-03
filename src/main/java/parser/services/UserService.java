package parser.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import parser.model.User;
import parser.model.enums.BotState;
import parser.repositories.UserRep;
import parser.services.cache.PageUserCache;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class UserService {

    UserRep userRep;
    PageUserCache pageUserCache;

    public void updateBotState(Long userId, BotState botState) {
        userRep.updateBotState(userId, botState);
        log.info("Update botState. userId: {}, botState: {}", userId, botState);
    }

    public List<User> findByDeleted(int pageNumber, int pageSize) {
        PageRequest pageable = PageRequest.of(pageNumber, pageSize);
        return userRep.findByDeletedFalse(pageable);
    }

    public void countPage(int pageSize){
        int sizePage = pageSize - 1;
        int allUsers = userRep.findByDeletedFalse().size();
        int countPage= 0;
        if (allUsers % sizePage != 0){
            countPage = (int) Math.ceil(allUsers / sizePage);
        }
        else {
            countPage = allUsers / sizePage;
        }
        pageUserCache.saveCountPage(countPage);
    }

    public void deleteSoft(Long userId) {
        userRep.softDelete(userId);
    }

    public boolean findByDeletedTrueAndUserId(Long userId) {
        User user = userRep.findById(userId).orElse(null);
        return userRep.findByDeletedTrue().contains(user);
    }

    public void recoverUser(Long userId) {
        userRep.recoverUser(userId);
    }

    public void deleteUser(){
        List<User> byDeletedAndWhenDeleted = userRep.findByDeletedAndWhenDeleted(LocalDate.now().minusDays(180));
        userRep.deleteAll(byDeletedAndWhenDeleted);
    }
    public void updateDayMailing(Long userId, int day){
        userRep.updateDayMailing(userId,day);
        log.info("Update DayMailing. userId: {},dayMailing: {}",userId,day);
    }

    public void saveNewUser(Long userId,String name){
        User user = new User();
        user.setUserId(userId);
        user.setName(name);
        userRep.save(user);
        log.info("Save new user. name: {}, userId: {}", name,userId);
    }

    //проверка времени активности
    public List<User> findByLocalTime(LocalDateTime dateTime,int minutes, BotState botState){
        return userRep.findByLocalTime(dateTime.minusMinutes(minutes),botState);
    }

    public User findUserById(Long userId){return userRep.findById(userId).orElse(null);}

    public void updateCountSi(Long userId,int count){
        userRep.updateCountSi(userId,count);
    }
}
