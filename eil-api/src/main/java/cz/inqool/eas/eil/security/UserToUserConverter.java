package cz.inqool.eas.eil.security;

import cz.inqool.eas.common.security.User;
import org.springframework.stereotype.Service;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserToUserConverter {

    public User convertToUser(cz.inqool.eas.eil.user.User user, boolean withAuthorities) {
        if (user == null ) {
            return null;
        }

        List<String> permissionsStr = new ArrayList<>();
        switch (user.getRole()) {
            case USER:
                permissionsStr.add(Permission.USER.name());
                break;
            case ADMIN:
                permissionsStr.add(Permission.USER.name());   //higher levels do get permissions of lower levels to allow for simpler permission checks later
                permissionsStr.add(Permission.ADMIN.name());
                break;
            case SUPER_ADMIN:
                permissionsStr.add(Permission.USER.name());
                permissionsStr.add(Permission.ADMIN.name());
                permissionsStr.add(Permission.SUPER_ADMIN.name());
                break;
        }

        Set<GrantedAuthority> permissions = null;
        if (withAuthorities) {
            permissions = permissionsStr.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());
        }

        String name = user.getFullName();
        if (name == null) {
            name = user.getEmail();
        }

        return new User(
                user.getId(),
                name,
                user.getEmail(),
                user.getPassword(),
                permissions,
                null,
                true,
                null
        );
    }
}