package utils;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

// Classe utilitária para gerar salt e aplicar hash em senhas
public class hashUtil {

    // Método que gera um salt aleatório de 16 bytes e o retorna como string Base64
    public static String gerarSalt() {
        byte[] salt = new byte[16]; // Cria um array de 16 bytes
        new SecureRandom().nextBytes(salt); // Preenche com bytes aleatórios
        return Base64.getEncoder().encodeToString(salt); // Codifica em Base64 e retorna como string
    }

    // Método que recebe uma senha e um salt, e retorna o hash SHA-256 da combinação
    public static String hashSenhaComSalt(String senha, String salt) {
        try {
            // Usa o algoritmo SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // Concatena o salt e a senha (salt + senha)
            String senhaComSalt = salt + senha;

            // Gera o hash como array de bytes
            byte[] hashBytes = md.digest(senhaComSalt.getBytes());

            // Converte o array de bytes em uma string hexadecimal (ex: a3f2d5...)
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString(); // Retorna o hash final
        } catch (Exception e) {
            // Em caso de falha, lança uma exceção com mensagem clara
            throw new RuntimeException("Erro ao gerar hash da senha com salt", e);
        }
    }
}
