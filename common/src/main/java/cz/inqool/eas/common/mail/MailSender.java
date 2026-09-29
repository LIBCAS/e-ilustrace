package cz.inqool.eas.common.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.*;

import static cz.inqool.eas.common.utils.AssertionUtils.ifPresent;

@Slf4j
public class MailSender {
    private MailSenderProperties properties;

    private Map<String, JavaMailSender> senders;
    private Map<String, MailSenderProperties.Sender> senderProperties;

    @PostConstruct
    public void init() {
        if (properties.getSenders().size() == 0) {
            log.warn("No Mail senders found. Mails will be silently ignored.");
        }

        this.senders = new LinkedHashMap<>();
        this.senderProperties = new LinkedHashMap<>();
        for (MailSenderProperties.Sender properties : properties.getSenders()) {
            JavaMailSenderImpl sender = new JavaMailSenderImpl();
            sender.setDefaultEncoding("UTF-8");
            sender.setHost(properties.getHost());
            sender.setPort(properties.getPort());

            sender.setUsername(properties.getUsername());
            sender.setPassword(properties.getPassword());

            ifPresent(properties.getProperties(), customProperties -> {
                Properties props = sender.getJavaMailProperties();
                props.putAll(customProperties);
            });

            this.senders.put(properties.getId(), sender);
            this.senderProperties.put(properties.getId(), properties);
        }
    }

    /**
     * Sends mail using all configured Mail Senders.
     *
     * @param mail Mail to send
     * @throws MessagingException if exception has occurred during mail sending
     */
    public void send(Mail mail) throws MessagingException {
        Iterator<String> iterator = senders.keySet().iterator();
        while (iterator.hasNext()) {
            String senderId = iterator.next();
            try {
                sendUsingSender(mail, senderId);
                log.debug("Email '{}' sent using sender '{}'.", mail.getId(), senderId);
                return;
            } catch (Exception e) {
                log.error("Failed to send email '{}' using sender '{}", mail.getId(), senderId, e);

                // rethrow for last sender
                if (!iterator.hasNext()) {
                    throw e;
                }
            }
        }
    }

    protected void sendUsingSender(Mail mail, String senderId) throws MessagingException {
        JavaMailSender sender = senders.get(senderId);
        MailSenderProperties.Sender properties = senderProperties.get(senderId);

        MimeMessageHelper helper = generateMessageHelper(sender, properties, false);

        helper.setTo(mail.getTo());
        helper.setSubject(mail.getSubject());
        helper.setText(mail.getContent(), Objects.equals(mail.getContentType(), "text/html"));

        MimeMessage message = helper.getMimeMessage();

        if (message.getAllRecipients() != null && message.getAllRecipients().length > 0) {
            sender.send(message);
        } else {
            log.warn("Mail message was silently consumed because there were no recipients.");
        }
    }

    /**
     * Creates message helper.
     *
     * The result is used during message transformation and sending.
     *
     * @param hasAttachment Will the email have attachment
     * @return Prepared message helper
     * @throws MessagingException if creation of message helper is not possible
     */
    protected MimeMessageHelper generateMessageHelper(JavaMailSender sender, MailSenderProperties.Sender properties, boolean hasAttachment) throws MessagingException {
        MimeMessage message = sender.createMimeMessage();

        // use the true flag to indicate you need a multipart message
        MimeMessageHelper helper = new MimeMessageHelper(message, hasAttachment);

        try {
            helper.setFrom(properties.getFromEmail(), properties.getFromName());
        } catch (UnsupportedEncodingException ex) {
            log.warn("Can not set email 'from' encoding, fallback.");
            helper.setFrom(properties.getFromEmail());
        }

        return helper;
    }

    @Autowired
    public void setProperties(MailSenderProperties properties) {
        this.properties = properties;
    }
}
