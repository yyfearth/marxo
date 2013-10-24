package marxo.entity;

import java.util.regex.Pattern;

public class User extends TenantChildEntity {
	public final static Pattern emailPattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
	public String password;
	public String email;
	UserType userType;

//	public String getEmail() {
//		return email;
//	}
//
//	public void setEmail(String email) throws InvalidArgumentException {
//		// todo: move the verification to other places.
//		Matcher matcher = emailPattern.matcher(email);
//		if (matcher.find()) {
//			this.email = email;
//		} else {
//			throw new InvalidArgumentException(new String[]{"email"});
//		}
//	}

	public boolean checkPassword(String password) {
		return this.password.equals(password);
	}

	@Override
	public void fillWithDefaultValues() {
		key = email;

		super.fillWithDefaultValues();
	}
}
