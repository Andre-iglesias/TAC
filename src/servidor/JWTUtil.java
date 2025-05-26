package servidor;

import java.util.Base64;

// Classe utilitária para codificação e decodificação Base64 no padrão URL-safe (usado em JWT)
public class JWTUtil {

    // Método para codificar um array de bytes em Base64 URL-safe, sem padding (sem "=" no final)
    public static String encode(byte[] bytes) {
        // Usa o codificador Base64 no formato URL-safe e remove os sinais de "=" ao final
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    // Método para decodificar uma string Base64 URL-safe de volta para bytes
    public static byte[] decode(String input) {
        // Usa o decodificador compatível com o formato URL-safe
        return Base64.getUrlDecoder().decode(input);
    }
}
