package cz.inqool.eas.eil.notification;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("eil.notifications")
public class NotificationProperties {

    private App app = new App();

    @Getter
    @Setter
    public static class App {

        /**
         * Application name
         */
        private String name;
    }
}
