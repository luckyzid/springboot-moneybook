package kr.money.book.user.web.domain.valueobject;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class PrincipalDetails implements OAuth2User, UserDetails {

    private final UserInfo user;
    private final Map<String, Object> attributes;
    private final String attributeKey;

    public PrincipalDetails(UserInfo user, Map<String, Object> attributes, String attributeKey) {
        this.user = user;
        this.attributes = attributes;
        this.attributeKey = attributeKey;
    }

    public UserInfo getUser() {

        return user;
    }

    @Override
    public String getName() {

        Object value = attributes.get(attributeKey);
        return Objects.toString(value, "UNKNOWN");
    }

    @Override
    public Map<String, Object> getAttributes() {

        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        if (user == null || user.role() == null) {
            return List.of();
        }

        return List.of(new SimpleGrantedAuthority(user.role().getKey()));
    }

    @Override
    public String getPassword() {

        return null;
    }

    @Override
    public String getUsername() {

        return user.userKey();
    }

    @Override
    public boolean isAccountNonExpired() {

        return true;
    }

    @Override
    public boolean isAccountNonLocked() {

        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {

        return true;
    }

    @Override
    public boolean isEnabled() {

        return true;
    }
}
