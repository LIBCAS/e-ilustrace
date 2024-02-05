package cz.inqool.eas.eil.init;

import cz.inqool.eas.common.init.DatedInitializer;
import cz.inqool.eas.eil.user.EilRole;
import cz.inqool.eas.eil.user.User;
import cz.inqool.eas.eil.user.UserRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@ConditionalOnProperty(prefix = "eil.init.demo", name = "enabled", havingValue = "true")
@Order(10)
@Component
@Slf4j
public class UserInitializer extends DatedInitializer<User, UserRepository> {
    public static final String USER1_ID = "d9107a46-4ff8-42fc-a1ce-9a0daff78879";
    public static final String USER2_ID = "8f5d2978-160e-449a-9e58-e3da2a679e76";

    @Getter
    private UserRepository repository;

    private PasswordEncoder passwordEncoder;

    @Override
    protected List<User> initializeEntities() {
        List<User> entries = new ArrayList<>();

        entries.add(newInstance(
                USER1_ID,
                "Mikuláš",
                "Konáč",
                "konac.mik@gmail.com",
                "user",
                EilRole.USER,
                true
                ));

        entries.add(newInstance(
                USER2_ID,
                "Mistr",
                "Nového zákona",
                "maestro@gmail.com",
                "admin",
                EilRole.ADMIN,
                true
        ));

        return entries;
    }

    private User newInstance(
        String id,
        String firstName,
        String lastName,
        String email,
        String password,
        EilRole role,
        boolean validated
    ) {
        User obj = findOrDefault(id, User.class);

        obj.setId(id);
        obj.setFirstName(firstName);
        obj.setLastName(lastName);
        obj.setEmail(email);
        obj.setPassword(passwordEncoder.encode(password));
        obj.setRole(role);
        obj.setValidated(validated);
        return obj;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.repository = userRepository;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
}
