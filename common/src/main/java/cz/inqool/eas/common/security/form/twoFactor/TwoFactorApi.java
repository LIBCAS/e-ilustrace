package cz.inqool.eas.common.security.form.twoFactor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Api for challenging single {@link TwoFactorSignIn}
 **/
@Tag(name = "Two factor sign in", description = "Kontrola dvou faktorového prihlášení")
@RestController
@RequestMapping("/two-factor")
@ConditionalOnProperty(prefix = "eas.security.form.two-factor", name = "enabled", havingValue = "true")
public class TwoFactorApi {

    private TwoFactorService service;

    @Operation(summary = "Challenge TwoFactorSignIn")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "400",
            description = "Problem with 2fa sign in",
            content = @Content(
                    mediaType = APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = TwoFactorException.class)))
    @PostMapping("/{code}/challenge")
    public void challenge(@PathVariable String code) {
        service.challenge(code);
    }

    @Autowired
    public void setService(TwoFactorService service) {
        this.service = service;
    }
}
