package vn.eledevo.vksbe.constant;

import static vn.eledevo.vksbe.constant.Permission.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    USER(Set.of(USER_READ, USER_CREATE, USER_UPDATE, USER_CANCEL)),
    ADMIN(Set.of(
            ADMIN_READ,
            ADMIN_UPDATE,
            ADMIN_DELETE,
            ADMIN_CREATE,
            MANAGER_READ,
            MANAGER_UPDATE,
            MANAGER_DELETE,
            MANAGER_CREATE)),
    MANAGER(Set.of(MANAGER_READ, MANAGER_UPDATE, MANAGER_DELETE, MANAGER_CREATE));
    private final Set<Permission> permissions;

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermissionValue()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}
