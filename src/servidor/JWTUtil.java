package servidor;
import java.util.Base64;

public class JWTUtil {
    public static String encode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
    public static byte[] decode(String input) {
        return Base64.getUrlDecoder().decode(input);
    }
}
