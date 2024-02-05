package cz.inqool.eas.common.security.form.twoFactor;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

/**
 * Class represents data associated with two factor authentication for single user
 **/
@Getter
@Setter
public class TwoFactorSignIn implements Serializable {

    private Instant created = Instant.now();
    private String userId;
    private String secret;
    private int attempt = 0;
}