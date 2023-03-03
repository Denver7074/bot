package parser.services.cache;

import org.springframework.stereotype.Service;
import parser.model.Verification;

import java.util.*;

@Service
public class UserVerificationNowCache {
    Map<Long, Verification> verificationUsersMap = new HashMap<>();

    public void saveUserVerificationNow(Long userId, Verification verification) {
        verificationUsersMap.remove(userId);
        verificationUsersMap.put(userId, verification);
    }

    public Verification getUserVerificationNow(Long userId) {
        return verificationUsersMap.get(userId);
    }

}
