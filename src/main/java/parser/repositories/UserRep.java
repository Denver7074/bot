package parser.repositories;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import parser.model.Mailing;
import parser.model.User;
import parser.model.enums.BotState;

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface UserRep extends JpaRepository<User, Long> {

    @Modifying
    @Query("update User u set u.deleted = true,u.whenDeleted = current_date where u.userId = :userId")
    void softDelete(@Param("userId") Long userId);

    @Modifying
    @Query("update User u set u.deleted = false, u.whenDeleted = null where u.userId = :userId")
    void recoverUser(@Param("userId") Long userId);

    List<User> findByDeletedFalse(PageRequest pageable);
    List<User> findByDeletedFalse();
    Set<User> findByDeletedTrue();

    @Query("select u from User u where u.deleted = true and u.whenDeleted < :date")
    List<User> findByDeletedAndWhenDeleted(@Param("date") LocalDate date);

    @Modifying
    @Query("update User u set u.botState = :botState, u.activity = current_timestamp where u.userId = :userId")
    void updateBotState(@Param("userId") Long userId, @Param("botState") BotState botState);

    @Modifying
    @Query("update User u set u.dayMailing = :day, u.activity = current_timestamp where u.userId = :userId")
    void updateDayMailing(@Param("userId") Long userId, @Param("day") int day);

    @Modifying
    @Query("update User u set u.countSi = u.countSi + :count where u.userId = :userId")
    void updateCountSi(@Param("userId") Long userId, @Param("count") int count);

    @Query("select u from User u where u.activity < :time and u.botState <> :botState")
    List<User> findByLocalTime(@Param("time") LocalDateTime time, @Param("botState") BotState botState);

}
