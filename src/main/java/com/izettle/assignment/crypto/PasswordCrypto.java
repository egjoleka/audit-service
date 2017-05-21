package com.izettle.assignment.crypto;

import static com.izettle.assignment.utils.ExceptionCreator.throwBadRequestException;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

import com.izettle.assignment.exception.IzettleException;

public class PasswordCrypto {

	public final static char[] CHAR_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
	public final static char[] CHAR_SPECIALS = { '!', '#', '$', '%', '&', '(', ')', '*', '+', '-', '.', ':', ';', '=',
			'?', '@', '^', '_', '|', '~' }; // Should be a sorted array, don't
											// just add at the end
	public final static char[] CHAR_UPPERS = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
			'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
	public final static char[] CHAR_LOWERS = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
			'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };

	public static byte[] getEncryptedPasswordByteArray(final String password, final byte[] salt) {
		// PBKDF2 with SHA-1 as the hashing algorithm. Note that the NIST
		// specifically names SHA-1 as an acceptable hashing algorithm for
		// PBKDF2
		final String algorithm = "PBKDF2WithHmacSHA256";
		// SHA-1 generates 160 bit hashes, so that's what makes sense here
		final int derivedKeyLength = 160;
		// Pick an iteration count that works for you. The NIST recommends at
		// least 1,000 iterations:
		// http://csrc.nist.gov/publications/nistpubs/800-132/nist-sp800-132.pdf
		// iOS 4.x reportedly uses 10,000:
		// http://blog.crackpassword.com/2010/09/smartphone-forensics-cracking-blackberry-backup-passwords/
		// https://www.owasp.org/index.php/Password_Storage_Cheat_Sheet
		final int iterations = 100000;

		final KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, derivedKeyLength);
		try {
			final SecretKeyFactory f = SecretKeyFactory.getInstance(algorithm);
			return f.generateSecret(spec).getEncoded();
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			throw new IzettleException(Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity("Problem while creating the encrypted password").build());
		}
	}

	public byte[] generateSalt() {
		// VERY important to use SecureRandom instead of just Random
		SecureRandom random;
		// Generate a 8 byte (64 bit) salt as recommended by RSA PKCS5
		final byte[] salt = new byte[8];
		try {
			random = SecureRandom.getInstance("SHA1PRNG");
			random.nextBytes(salt);
		} catch (final NoSuchAlgorithmException e) {
			throw new IzettleException(Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity("Problem while creating the salt for encrypted password").build());
		}

		return salt;
	}

	public static String getEncryptedPassword(final String password, final byte[] salt) {
		return Base64.encodeBase64String(getEncryptedPasswordByteArray(password, salt));
	}

	public static void validatePasswordPolicy(final String userName, final String password,
			final String confirmPassword) {
		validatePassword(userName, password, "Password");
		if (!password.equals(confirmPassword)) {
			throwBadRequestException("Password mismatched");
		}
	}

	public static void validatePassword(final String userName, final String password, final String field) {
		checkLength(password, field);
		checkSimilarityToUserName(userName, password, field);
		verifyPasswordStrength(password, field);
	}

	private static void checkLength(final String password, final String field) {
		if (StringUtils.isBlank(password) || password.length() < 8) {
			throwBadRequestException(field + " should be at least 8 characters long");
		}
		if (StringUtils.isBlank(password) || password.length() > 160) {
			throwBadRequestException(field + " should not be longer than 160 characters");
		}
	}

	private static void checkSimilarityToUserName(final String userName, final String password, final String field) {
		if (StringUtils.isBlank(userName)) {
			throwBadRequestException("userName cannot be null or empty");
		}
		if (StringUtils.isBlank(password)) {
			throwBadRequestException(field + " cannot be null or empty");
		}
		if (password.equalsIgnoreCase(userName)) {
			throwBadRequestException(field + " should not be similar to user name");
		}
		final int length = password.length();
		for (int i = 0; i < length - 4; i++) {
			String sub = password.substring(i, i + 5);
			if (userName.indexOf(sub) > -1) {
				throwBadRequestException(field + " should not be similar to user name");
			}
		}
	}

	private static void verifyPasswordStrength(final String password, final String field) {
		// new password must have enough character sets and length
		checkStrength(password, CHAR_LOWERS, field);
		checkStrength(password, CHAR_UPPERS, field);
		checkStrength(password, CHAR_DIGITS, field);
		checkStrength(password, CHAR_SPECIALS, field);
	}

	private static void checkStrength(final String password, final char[] requiredCharSet, final String field) {
		int charsets = 0;
		for (int i = 0; i < password.length(); i++) {
			if (Arrays.binarySearch(requiredCharSet, password.charAt(i)) >= 0) {
				charsets++;
				break;
			}
		}
		if (charsets == 0) {
			throwBadRequestException(field
					+ " is not strong enough, should include lower case, upper case, digit and special characters");
		}
	}
}
