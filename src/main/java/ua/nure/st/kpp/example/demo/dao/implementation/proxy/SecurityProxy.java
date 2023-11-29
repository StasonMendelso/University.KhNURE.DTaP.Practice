package ua.nure.st.kpp.example.demo.dao.implementation.proxy;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import ua.nure.st.kpp.example.demo.entity.Role;
import ua.nure.st.kpp.example.demo.exception.AccessForbiddenException;

/**
 * @author Stanislav Hlova
 */
public abstract class SecurityProxy {

    protected final boolean hasRole(UserDetails userDetails, Role role) {
        return userDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(role.name()));
    }

    protected void checkAccessForModification(UserDetails userDetails) {
        if (userDetails == null || hasRole(userDetails, Role.USER)) {
            throw new AccessForbiddenException("Only Admins can modify data");
        }
    }
    protected UserDetails getUserDetails(){
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return (UserDetails) securityContext.getAuthentication().getPrincipal();
    }

}
