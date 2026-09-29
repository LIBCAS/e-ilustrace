package cz.inqool.eas.common.mail;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Properties;

@ConfigurationProperties(prefix = "eas.mail")
@Getter
@Setter
public class MailSenderProperties {
    private List<Sender> senders;

    @Getter
    @Setter
    public static class Sender {
        String id;

        String host;
        Integer port;

        String username;
        String password;

        String fromName;
        String fromEmail;

        Properties properties;
    }
}
