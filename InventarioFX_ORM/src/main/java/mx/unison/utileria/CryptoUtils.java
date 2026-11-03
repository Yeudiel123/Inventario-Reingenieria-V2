package mx.unison.utileria;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/** Contraseñas con salt individual; admite SHA-256 antiguo para migrarlo al ingresar. */
public final class CryptoUtils {
    private static final String PREFIX = "pbkdf2-sha256";
    private static final int ITERATIONS = 600_000;
    private static final SecureRandom RANDOM = new SecureRandom();

    private CryptoUtils() {}

    public static String hashPassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía.");
        }
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return PREFIX + "$" + ITERATIONS + "$" + Base64.getEncoder().encodeToString(salt)
                + "$" + Base64.getEncoder().encodeToString(derive(password, salt, ITERATIONS));
    }

    public static boolean verifyPassword(String password, String storedHash) {
        if (password == null || password.isEmpty() || storedHash == null) {
            return false;
        }
        try {
            if (!storedHash.startsWith(PREFIX + "$")) {
                byte[] expected = Base64.getDecoder().decode(storedHash);
                if (expected.length != 32) return false;
                byte[] actual = MessageDigest.getInstance("SHA-256")
                        .digest(password.getBytes(StandardCharsets.UTF_8));
                if (MessageDigest.isEqual(expected, actual)) return true;
                // Java 17 en Windows utilizaba Windows-1252 como codificación predeterminada.
                byte[] legacyWindows = MessageDigest.getInstance("SHA-256")
                        .digest(password.getBytes(Charset.forName("windows-1252")));
                return MessageDigest.isEqual(expected, legacyWindows);
            }
            String[] parts = storedHash.split("\\$", -1);
            if (parts.length != 4) return false;
            int iterations = Integer.parseInt(parts[1]);
            if (iterations < 1 || iterations > 2_000_000) return false;
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expected = Base64.getDecoder().decode(parts[3]);
            if (salt.length != 16 || expected.length != 32) return false;
            return MessageDigest.isEqual(expected, derive(password, salt, iterations));
        } catch (IllegalArgumentException | GeneralSecurityException e) {
            return false;
        }
    }

    public static boolean needsRehash(String storedHash) {
        if (storedHash == null || !storedHash.startsWith(PREFIX + "$")) return true;
        try {
            return Integer.parseInt(storedHash.split("\\$", -1)[1]) < ITERATIONS;
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            return true;
        }
    }

    private static byte[] derive(String password, byte[] salt, int iterations) {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, 256);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("PBKDF2 no está disponible en este entorno Java.", e);
        } finally {
            spec.clearPassword();
        }
    }
}
