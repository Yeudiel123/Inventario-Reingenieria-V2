package mx.unison;

import mx.unison.utileria.CryptoUtils;
import org.junit.jupiter.api.Test;
import java.nio.charset.StandardCharsets;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.Base64;
import static org.junit.jupiter.api.Assertions.*;

class CryptoUtilsTest {
    @Test
    void usaSaltDistintoYVerificaLaPassword() {
        String password = "Contraseña larga de prueba";
        String first = CryptoUtils.hashPassword(password);
        String second = CryptoUtils.hashPassword(password);
        assertNotEquals(first, second);
        assertTrue(CryptoUtils.verifyPassword(password, first));
        assertTrue(CryptoUtils.verifyPassword(password, second));
        assertFalse(CryptoUtils.verifyPassword("otra contraseña", first));
        assertFalse(CryptoUtils.needsRehash(first));
    }

    @Test
    void permiteMigrarSha256Legacy() throws Exception {
        String legacy = Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-256")
                .digest("admin123".getBytes(StandardCharsets.UTF_8)));
        assertTrue(CryptoUtils.verifyPassword("admin123", legacy));
        assertTrue(CryptoUtils.needsRehash(legacy));
        assertFalse(CryptoUtils.verifyPassword("incorrecta", legacy));
    }

    @Test
    void conservaPasswordsLegacyLargasYConAcentosDeWindows() throws Exception {
        String password = "Contraseña con acentos áéíóú " + "x".repeat(129);
        String legacy = Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-256")
                .digest(password.getBytes(Charset.forName("windows-1252"))));
        assertTrue(CryptoUtils.verifyPassword(password, legacy));
        String upgraded = CryptoUtils.hashPassword(password);
        assertTrue(CryptoUtils.verifyPassword(password, upgraded));
        assertFalse(CryptoUtils.needsRehash(upgraded));
    }

    @Test
    void rechazaHashesDañadosSinLanzarExcepcion() {
        for (String hash : new String[]{"", "no es base64", "pbkdf2-sha256$abc$x$x",
                "pbkdf2-sha256$-1$x$x", "pbkdf2-sha256$2147483647$x$x",
                "pbkdf2-sha256$600000$AA==$AA==", "pbkdf2-sha256$600000"}) {
            assertFalse(CryptoUtils.verifyPassword("password", hash), hash);
        }
        assertFalse(CryptoUtils.verifyPassword(null, null));
        assertThrows(IllegalArgumentException.class, () -> CryptoUtils.hashPassword(""));
    }
}
