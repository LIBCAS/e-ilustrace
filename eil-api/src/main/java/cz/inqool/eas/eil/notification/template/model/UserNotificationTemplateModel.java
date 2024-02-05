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
        "user",
})
public class UserNotificationTemplateModel extends EmailModel {

    @NotNull
    @Valid
    @JsonProperty
    @JsonPropertyDescription("Uživatel")
    protected UserModel user;

    public UserNotificationTemplateModel(User user, String token) {
        this.user = UserModel.of(user, token);
    }

    @Getter
    @Setter
    @JsonPropertyOrder({
            "firstName",
            "lastName",
            "email",
            "token",
    })
    public static class UserModel {

        @NotNull
        @JsonProperty(defaultValue = "Jan")
        @JsonPropertyDescription("Jméno")
        private String firstName;

        @NotNull
        @JsonProperty(defaultValue = "Novák")
        @JsonPropertyDescription("Příjmení")
        private String lastName;

        @NotNull
        @JsonProperty(defaultValue = "jan.novak@email.cz")
        @JsonPropertyDescription("E-mail")
        private String email;

        @JsonProperty(defaultValue = "c1c4c64f-ccca-4bf7-9ddd-3bc003deee98")
        @JsonPropertyDescription("Potvrzovací kód")
        protected String token;

        public static UserModel of(User user) {
            var model = new UserModel();
            if (user != null) {
                model.setFirstName(user.getFirstName());
                model.setLastName(user.getLastName());
                model.setEmail(user.getEmail());
            }

            return model;
        }

        public static UserModel of(User user, String token) {
            var model = of(user);
            model.setToken(token);
            return model;
        }
    }
}