package cz.inqool.eas.eil.security;

import cz.inqool.eas.eil.domain.LabeledEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Permission implements LabeledEnum<Permission> {
    USER("Uživatel"),
    ADMIN("Admin"),
    SUPER_ADMIN("Super admin"),
    ;

    @Getter
    private final String label;
}
