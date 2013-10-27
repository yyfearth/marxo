package marxo.security;

import marxo.entity.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class MarxoAuthentication extends UsernamePasswordAuthenticationToken {
	User user;

	public MarxoAuthentication(User user) {
		super(user.getEmail(), user.getPassword());
		this.user = user;
	}

	public MarxoAuthentication(User user, Collection<? extends GrantedAuthority> authorities) {
		super(user.getEmail(), user.getPassword(), authorities);
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public Object getCredentials() {
		return user.getPassword();
	}

	@Override
	public Object getPrincipal() {
		return user.getEmail();
	}

	@Override
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
		super.setAuthenticated(isAuthenticated);
	}

	@Override
	public void eraseCredentials() {
		user.setPassword(null);
	}
}
