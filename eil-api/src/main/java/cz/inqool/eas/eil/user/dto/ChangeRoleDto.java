package cz.inqool.eas.eil.user.dto;

import cz.inqool.eas.eil.user.EilRole;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ChangeRoleDto {
    @NotNull
    private String userId;
    @NotNull
    private EilRole role;
}
