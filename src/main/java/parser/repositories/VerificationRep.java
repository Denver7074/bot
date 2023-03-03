package parser.repositories;

import jakarta.persistence.TypedQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import parser.model.User;
import parser.model.Verification;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public interface VerificationRep extends JpaRepository<Verification, Long> {

    @Modifying
    @Query("UPDATE Verification v SET v.writingAboutVerification = :date1, v.idVerification = :id, v.updateVerification = current_date, v.validDate = :validDate,v.verificationDate = :verificationDate,v.applicability = :applicability, v.href = :href, v.orgTitle = :orgTitle  where v.mitNumber = :mitNumber and v.number = :number")
    void updateVerification(@Param("date1") LocalDate date1, @Param("id") String id, @Param("validDate") LocalDate validDate, @Param("verificationDate") LocalDate verificationDate, @Param("applicability") boolean applicability, @Param("mitNumber") String mitNumber, @Param("href") String href, @Param("orgTitle") String orgTitle, @Param("number") String number);

    @Query("select v from Verification v where v.user.deleted = false and (v.writingAboutVerification = :date or v.validDate = :date)")
    List<Verification> findByWritingAboutVerificationOOrValidDate(@Param("date") LocalDate date);

    @Query("select v from Verification v where v.user.userId = :userId and v.mitNumber = :mitNumber and v.number = :number")
    Verification findByUserAndMitNumberAndNumber(@Param("userId") Long userId, @Param("mitNumber") String mitNumber, @Param("number") String number);

    @Query("select v from Verification v where v.user.userId = :userId")
    List<Verification> findAllInstrument(@Param("userId") Long userId);

    @Modifying
    @Query("delete from Verification v where v.idVerification = :id")
    void deleteByIdVerification(@Param("id") String id);

    @Modifying
    @Query("update Verification v set v.writingAboutVerification = :date where v.idVerification = :id")
    void updateDateBefore(@Param("id") String id, @Param("date") LocalDate date);

    @Query("select v from Verification v where v.validDate < :date and v.user.deleted = false")
    List<Verification> findFinishValidDate(@Param("date") LocalDate date);

    @Query("select v from Verification v where v.updateVerification = :date and v.user.deleted = false")
    List<Verification> findActualUpdate(@Param("date") LocalDate date);

    @Modifying
    @Query("update Verification v set v.miType = :miType where v.idVerification = :idVerification")
    void updateMiType(@Param("idVerification") String idVerification, @Param("miType") String miType);
}
