package cz.inqool.eas.common.security;

import cz.inqool.eas.common.security.User;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/me")
public class MeApi {
    @GetMapping
    public User me(Authentication authentication) {
        if (authentication != null) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof User) {
                return (User) principal;
            }
        }
        return null;
    }
}
