package vn.ute.utils;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import vn.ute.constants.AppConstants_24162130;

/** Gui ma OTP qua email (SMTP Gmail, STARTTLS cong 587). */
public final class MailUtil_24162130 {

    private static final Logger LOG = Logger.getLogger(MailUtil_24162130.class.getName());

    private MailUtil_24162130() {
    }

    public static boolean isConfigured() {
        return !AppConstants_24162130.MAIL_USERNAME.isEmpty() && !AppConstants_24162130.MAIL_PASSWORD.isEmpty();
    }

    /**
     * Gui OTP. Tra ve true neu gui thanh cong.
     * Neu chua cau hinh SMTP hoac gui loi: ghi OTP ra log de test (chi dung khi phat trien).
     */
    public static boolean sendOtp(String toEmail, String fullname, String otp) {
        if (!isConfigured()) {
            LOG.warning("[DEV] Chua cau hinh SMTP. OTP cua " + toEmail + " la: " + otp);
            return false;
        }
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", AppConstants_24162130.MAIL_HOST);
            props.put("mail.smtp.port", AppConstants_24162130.MAIL_PORT);
            props.put("mail.smtp.connectiontimeout", "10000");
            props.put("mail.smtp.timeout", "10000");
            props.put("mail.smtp.writetimeout", "10000");

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(AppConstants_24162130.MAIL_USERNAME,
                            AppConstants_24162130.MAIL_PASSWORD);
                }
            });

            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(AppConstants_24162130.MAIL_USERNAME));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
            msg.setSubject("Mã OTP kích hoạt tài khoản Shop Online", "UTF-8");
            msg.setText("Xin chào " + fullname + ",\n\n"
                    + "Mã OTP kích hoạt tài khoản của bạn là: " + otp + "\n"
                    + "Mã có hiệu lực trong " + AppConstants_24162130.OTP_VALID_MINUTES + " phút.\n"
                    + "Vui lòng không chia sẻ mã này cho bất kỳ ai.\n", "UTF-8");
            Transport.send(msg);
            return true;
        } catch (MessagingException e) {
            LOG.log(Level.WARNING, "Gui mail that bai", e);
            LOG.warning("[DEV] OTP cua " + toEmail + " la: " + otp);
            return false;
        }
    }
}
