package cz.inqool.eas.eil.user.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ChangePasswordDto {
    @NotNull
    private String password;
}
