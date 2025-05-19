package utils;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class hashUtil {

    // Gera um salt aleatório
    public static String gerarSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    // Gera o hash com o salt
    public static String hashSenhaComSalt(String senha, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String senhaComSalt = salt + senha;
            byte[] hashBytes = md.digest(senhaComSalt.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar hash da senha com salt", e);
        }
    }
}

