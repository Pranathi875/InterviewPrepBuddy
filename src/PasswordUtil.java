import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Password hashing utility using SHA-256 + salt.
 * Each password gets a unique random salt, so even identical passwords
 * produce different hashes in the database.
 *
 * Stored format: salt:hash (both Base64 encoded)
 */
public class PasswordUtil {

    private static final int SALT_LENGTH = 16;

    /**
     * Hashes a plain-text password with a random salt.
     * @return "salt:hash" string to store in database
     */
    public static String hashPassword(String password) {
        byte[] salt = generateSalt();
        String hash = hash(password, salt);
        String saltBase64 = Base64.getEncoder().encodeToString(salt);
        return saltBase64 + ":" + hash;
    }

    /**
     * Verifies a plain-text password against a stored "salt:hash" string.
     */
    public static boolean verifyPassword(String password, String storedHash) {
        String[] parts = storedHash.split(":");
        if (parts.length != 2) return false;

        byte[] salt = Base64.getDecoder().decode(parts[0]);
        String expectedHash = parts[1];
        String actualHash = hash(password, salt);

        return expectedHash.equals(actualHash);
    }

    private static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }

    private static String hash(String password, byte[] salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt);
            byte[] hashBytes = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
