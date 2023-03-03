package parser.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import parser.model.Mailing;
import parser.model.User;

import java.util.List;

public interface MailingRep extends JpaRepository<Mailing, Long> {
    Mailing findByUserAndEmail(User user, String email);

    @Query("select m from Mailing m where m.user.userId = :userId")
    List<Mailing> findAllMailing(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE from Mailing m where m.user.userId = :userId and m.email = :email")
    void unplugEmail(@Param("userId") Long userId, @Param("email") String email);
}
