package cz.inqool.eas.eil.security;

import cz.inqool.eas.common.security.User;
import cz.inqool.eas.eil.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/me")
public class EilMeApi {

    @Autowired
    UserRepository userRepository;
    @Autowired
    UserToUserConverter userToUserConverter;

    @GetMapping
    public User me(Authentication authentication) {
        if (authentication != null) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof User) {
                cz.inqool.eas.eil.user.User userEil = userRepository.find(((User) principal).getId());
                return userToUserConverter.convertToUser(userEil, true);
            }
        }
        return null;
    }
}
