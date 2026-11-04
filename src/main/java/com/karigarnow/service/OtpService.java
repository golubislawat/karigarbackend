package com.karigarnow.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class OtpService {

    private final SecureRandom random = new SecureRandom();
    private final Map<String, OtpEntry> otpStore = new ConcurrentHashMap<>();
    private static final long OTP_VALIDITY_SECONDS = 300; // 5 minutes

    public record OtpEntry(String code, String purpose, Instant expiresAt, int attempts) {
        public boolean isExpired() {
            return Instant.now().isAfter(expiresAt);
        }
    }

    public String generateAndSaveOtp(String email, String purpose) {
        String cleanEmail = email.trim().toLowerCase();
        int num = 100000 + random.nextInt(900000);
        String code = String.valueOf(num);

        otpStore.put(cleanEmail, new OtpEntry(
                code,
                purpose.toUpperCase(),
                Instant.now().plusSeconds(OTP_VALIDITY_SECONDS),
                0
        ));

        log.info("Generated 6-digit OTP for {} [{}]: {}", cleanEmail, purpose, code);
        return code;
    }

    public boolean verifyOtp(String email, String inputCode, String expectedPurpose) {
        String cleanEmail = email.trim().toLowerCase();
        OtpEntry entry = otpStore.get(cleanEmail);

        if (entry == null) {
            log.warn("No OTP found for {}", cleanEmail);
            return false;
        }

        if (entry.isExpired()) {
            otpStore.remove(cleanEmail);
            log.warn("OTP for {} has expired", cleanEmail);
            return false;
        }

        if (!entry.purpose().equalsIgnoreCase(expectedPurpose)) {
            log.warn("OTP purpose mismatch for {}. Expected: {}, Found: {}", cleanEmail, expectedPurpose, entry.purpose());
            return false;
        }

        if (entry.attempts() >= 5) {
            otpStore.remove(cleanEmail);
            log.warn("Too many failed OTP attempts for {}", cleanEmail);
            return false;
        }

        boolean matches = entry.code().equals(inputCode.trim());
        if (matches) {
            otpStore.remove(cleanEmail); // consume OTP on success
            return true;
        } else {
            otpStore.put(cleanEmail, new OtpEntry(entry.code(), entry.purpose(), entry.expiresAt(), entry.attempts() + 1));
            return false;
        }
    }

    public String getStoredOtpForTesting(String email) {
        String cleanEmail = email.trim().toLowerCase();
        OtpEntry entry = otpStore.get(cleanEmail);
        return entry != null && !entry.isExpired() ? entry.code() : null;
    }
}
