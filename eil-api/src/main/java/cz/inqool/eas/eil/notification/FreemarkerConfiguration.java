package cz.inqool.eas.eil.notification;

import no.api.freemarker.java8.Java8ObjectWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.annotation.PostConstruct;

@Configuration
public class FreemarkerConfiguration {

    private FreeMarkerConfigurer configurer;

    @PostConstruct
    public void customizeFreemarker() {
        configurer.getConfiguration().setObjectWrapper(new Java8ObjectWrapper(freemarker.template.Configuration.getVersion()));
    }

    @Autowired
    public void setConfigurer(FreeMarkerConfigurer configurer) {
        this.configurer = configurer;
    }
}
