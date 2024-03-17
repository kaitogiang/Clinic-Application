package entity;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordEncryptor {

	public static String hashPassword(String plainPassword) {
		String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
		return hashedPassword;
	}
	public static boolean checkPassword(String plainPassword, String hashedPassword ) {
		return BCrypt.checkpw(plainPassword, hashedPassword);
	}
}
