package cz.inqool.eas.eil.notification.template.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Template model containing common notification model properties
 */
@Setter
@Getter
@JsonPropertyOrder({
        "timestamp"
})
public abstract class CommonModel implements NotificationTemplateModel {

    @NotNull
    @JsonProperty(defaultValue = "2011-12-03T10:15:30")
    @JsonPropertyDescription("Datum a čas odeslání notifikace")
    protected LocalDateTime timestamp;
}
