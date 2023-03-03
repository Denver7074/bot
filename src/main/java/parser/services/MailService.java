package parser.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import parser.model.Mailing;
import parser.model.User;
import parser.repositories.MailingRep;

import java.util.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class MailService {
    MailingRep mailingRep;

    public List<Mailing> findAllMailing(Long userId) {
        return mailingRep.findAllMailing(userId);
    }

    public void saveEmail(User user, String email) {
        if (mailingRep.findByUserAndEmail(user, email) == null) {
            Mailing mailing = new Mailing();
            mailing.setEmail(email);
            mailing.setUser(user);
            mailingRep.save(mailing);
            log.info("Plug notification: userId{}, email: {}", user.getUserId(), email);
        }
    }

    public void unplugEmail(Long userId, String email) {
        mailingRep.unplugEmail(userId, email);
        log.info("Unplug notification: userId{}, email: {}", userId, email);
    }
}
