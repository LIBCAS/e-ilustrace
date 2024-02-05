package cz.inqool.eas.eil.security;

import cz.inqool.eas.common.exception.v2.ForbiddenOperation;
import cz.inqool.eas.common.security.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.HashSet;

import static cz.inqool.eas.common.utils.AssertionUtils.notNull;
import static cz.inqool.eas.eil.exception.EilExceptionCode.MISSING_PERMISSION_FOR_OPERATION;

public class UserChecker {

    public static void checkUserIsLoggedIn() {
        notNull(getUser(), () -> new ForbiddenOperation(MISSING_PERMISSION_FOR_OPERATION));
    }

    public static void checkUserHasAnyPermission(Permission... permissions) {
        if (!userHasAnyPermission(permissions)) {
            throw new ForbiddenOperation(MISSING_PERMISSION_FOR_OPERATION);
        }
    }

    public static boolean userHasAnyPermission(Permission... permissions) {
        for (Permission permission : permissions) {
            if (userHasPermission(permission, true)) {
                return true;
            }
        }
        return false;
    }

    public static boolean userHasPermission(Permission permission, boolean includeRestricted) {
        User user = getUser();
        if (user == null) {
            return false;
        }
        if (user.getAuthorities() == null) {
            return false;
        }
        Collection<GrantedAuthority> authorities = new HashSet<>(user.getAuthorities());
        for (GrantedAuthority authority : authorities) {
            String authorityStr = authority.getAuthority();
            if (includeRestricted && authorityStr.contains("@")) {
                authorityStr = authorityStr.substring(0, authorityStr.indexOf("@"));
            }
            if (authorityStr.equals(permission.name())) {
                return true;
            }
        }
        return false;
    }

    public static User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof User) {
                return (User) principal;
            }
        }
        return null;
    }

    public static String getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof User) {
                return ((User) principal).getId();
            }
        }
        return null;
    }
}
