package vn.ute.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;

import vn.ute.constants.AppConstants_24162130;

/**
 * Sinh / kiem tra OTP.
 * Bang Users chi co cot "code" (nvarchar 50) nen luu gop: "OTP|het_han_ms|so_lan_sai".
 */
public final class OtpUtil_24162130 {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final long VALID_MS = AppConstants_24162130.OTP_VALID_MINUTES * 60_000L;

    private OtpUtil_24162130() {
    }

    /** Trang thai OTP doc tu cot code. */
    public record OtpState(String otp, long expiresAt, int attempts) {

        public boolean isExpired() {
            return System.currentTimeMillis() > expiresAt;
        }

        /** Thoi diem OTP duoc phat hanh. */
        public long issuedAt() {
            return expiresAt - VALID_MS;
        }
    }

    /** OTP 6 chu so. */
    public static String generate() {
        return String.format("%06d", RANDOM.nextInt(1_000_000));
    }

    /** Chuoi luu vao cot code cho OTP moi phat hanh (het han sau OTP_VALID_MINUTES phut). */
    public static String newCode(String otp) {
        return encode(otp, System.currentTimeMillis() + VALID_MS, 0);
    }

    public static String encode(String otp, long expiresAt, int attempts) {
        return otp + "|" + expiresAt + "|" + attempts;
    }

    /** Tra ve null neu chuoi khong hop le. */
    public static OtpState parse(String code) {
        if (code == null) {
            return null;
        }
        String[] p = code.split("\\|");
        if (p.length != 3) {
            return null;
        }
        try {
            return new OtpState(p[0], Long.parseLong(p[1]), Integer.parseInt(p[2]));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** So sanh OTP theo thoi gian khong doi. */
    public static boolean matches(String expected, String input) {
        if (expected == null || input == null) {
            return false;
        }
        return MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8),
                input.trim().getBytes(StandardCharsets.UTF_8));
    }
}
