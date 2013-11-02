package marxo.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class PasswordEncryptor implements ILoggable {
	// review: increase the following numbers in production, in order to better secure the users.
	static final int iterationCount = 64;
	static final int keyLength = 128;
	final byte[] salt;
	final SecretKeyFactory secretKeyFactory;

	public PasswordEncryptor(byte[] salt, SecretKeyFactory secretKeyFactory) {
		this.salt = salt;
		this.secretKeyFactory = secretKeyFactory;
	}

	public PasswordEncryptor(String salt, SecretKeyFactory secretKeyFactory) {
		this.salt = DatatypeConverter.parseHexBinary(salt);
		this.secretKeyFactory = secretKeyFactory;
	}

	public String encrypt(String originalPassword) {
		KeySpec spec = new PBEKeySpec(originalPassword.toCharArray(), salt, iterationCount, keyLength);

		try {
			byte[] secret = secretKeyFactory.generateSecret(spec).getEncoded();
			return DatatypeConverter.printHexBinary(secret);
		} catch (InvalidKeySpecException e) {
			logger.error("Fail to encrypt the password", e.getMessage());
			return null;
		}
	}
}
