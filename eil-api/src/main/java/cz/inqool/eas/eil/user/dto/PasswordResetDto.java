package cz.inqool.eas.eil.user.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class PasswordResetDto {
    @NotNull
    private String key;
    @NotNull
    private String newPassword;
}
