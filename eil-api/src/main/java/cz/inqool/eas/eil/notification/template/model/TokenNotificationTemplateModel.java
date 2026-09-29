package cz.inqool.eas.eil.notification.template.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import cz.inqool.eas.eil.user.User;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@JsonPropertyOrder({
        "token",
})
public class TokenNotificationTemplateModel extends UserNotificationTemplateModel {

    @NotNull
    @Valid
    @JsonProperty(defaultValue = "167c4475-6e5b-48aa-ba22-18d8d5d18f39")
    @JsonPropertyDescription("Token pro potvrzení")
    protected String token;

    public TokenNotificationTemplateModel(User user, String token) {
        super(user, token);
        this.token = token;
    }
}
