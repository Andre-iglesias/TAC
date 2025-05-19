package servidor;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class JWTHmac {
    public static String createJWT(String payloadJson, String secret) throws Exception {
        String headerJson = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String header = JWTUtil.encode(headerJson.getBytes(StandardCharsets.UTF_8));
        String payload = JWTUtil.encode(payloadJson.getBytes(StandardCharsets.UTF_8));
        String headerPayload = header + "." + payload;
        Mac hmac = Mac.getInstance("HmacSHA256");
        hmac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] sig = hmac.doFinal(headerPayload.getBytes(StandardCharsets.UTF_8));
        return headerPayload + "." + JWTUtil.encode(sig);
    }

    public static boolean validateJWT(String jwt, String secret) throws Exception {
        String[] parts = jwt.split("\\.");
        if (parts.length != 3) return false;
        String headerPayload = parts[0] + "." + parts[1];
        Mac hmac = Mac.getInstance("HmacSHA256");
        hmac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] sig = hmac.doFinal(headerPayload.getBytes(StandardCharsets.UTF_8));
        String expectedSig = JWTUtil.encode(sig);
        return expectedSig.equals(parts[2]);
    }
}
