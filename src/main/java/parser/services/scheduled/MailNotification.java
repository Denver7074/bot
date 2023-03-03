package parser.services.scheduled;

import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import parser.model.Constant;
import parser.model.Mailing;
import parser.model.User;
import parser.model.Verification;
import parser.services.MailService;
import parser.services.verification.VerificationService;
import parser.utils.TextUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class MailNotification {
    JavaMailSender mailSender;
    VerificationService verificationService;
    MailService mailService;

    //@Scheduled(cron = "${cron.finishVerification}")
    public void sendEmailFinishVerification() {
        Map<User, List<Verification>> mapBefore = verificationService.findBeforeFinishVerification(LocalDate.now());
        if (!mapBefore.isEmpty()) {
            for (User u : mapBefore.keySet()) {
                List<Mailing> allMailing = mailService.findAllMailing(u.getUserId());
                for (Mailing m : allMailing) {
                    String text = TextUtil.stringUtil(mapBefore.get(u));
                    String email = m.getEmail();
                    mimeMessage(email, Constant.FIRST_ANSWER, text);
                }
            }
        }
    }

    //@Scheduled(cron = "${cron.finishVerification}")
    public void sendEmailUpdateVerification() {
        Map<User, List<Verification>> mapUpdate = verificationService.autoUpdateVerification(LocalDate.now());
        if (!mapUpdate.isEmpty()) {
            for (User u : mapUpdate.keySet()) {
                List<Mailing> allMailing = mailService.findAllMailing(u.getUserId());
                for (Mailing m : allMailing) {
                    String email = m.getEmail();
                    String text = TextUtil.stringUtil(mapUpdate.get(u));
                    mimeMessage(email, Constant.UPDATE_ANSWER, text);
                }
            }
        }
    }

    @SneakyThrows
    public void mimeMessage(String emailTo, String subject, String text) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true);
        message.setFrom("mailnotificationbot63@gmail.com");
        message.setTo(emailTo);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(mimeMessage);
    }

}
