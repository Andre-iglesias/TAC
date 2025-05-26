package servidor;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

// Classe responsável por criar e validar tokens JWT com assinatura HMAC-SHA256
public class JWTHmac {

    // Método para criar um JWT a partir de um payload (JSON) e uma chave secreta
    public static String createJWT(String payloadJson, String secret) throws Exception {
        // Define o cabeçalho JWT: algoritmo e tipo
        String headerJson = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";

        // Codifica o header e o payload em Base64URL (provavelmente usando JWTUtil.encode)
        String header = JWTUtil.encode(headerJson.getBytes(StandardCharsets.UTF_8));
        String payload = JWTUtil.encode(payloadJson.getBytes(StandardCharsets.UTF_8));

        // Junta os dois com um ponto para formar a primeira parte do token (header.payload)
        String headerPayload = header + "." + payload;

        // Cria o objeto HMAC com algoritmo HmacSHA256
        Mac hmac = Mac.getInstance("HmacSHA256");

        // Inicializa o HMAC com a chave secreta fornecida
        hmac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));

        // Gera o hash (assinatura) da string header.payload
        byte[] sig = hmac.doFinal(headerPayload.getBytes(StandardCharsets.UTF_8));

        // Retorna o token completo: header.payload.signature (todos codificados)
        return headerPayload + "." + JWTUtil.encode(sig);
    }

    // Método para validar um JWT usando a mesma chave secreta
    public static boolean validateJWT(String jwt, String secret) throws Exception {
        // Divide o token em suas três partes: header, payload, signature
        String[] parts = jwt.split("\\.");
        if (parts.length != 3) return false; // Token malformado

        // Junta header e payload novamente
        String headerPayload = parts[0] + "." + parts[1];

        // Cria um novo HMAC para comparar a assinatura
        Mac hmac = Mac.getInstance("HmacSHA256");
        hmac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));

        // Gera a assinatura esperada
        byte[] sig = hmac.doFinal(headerPayload.getBytes(StandardCharsets.UTF_8));
        String expectedSig = JWTUtil.encode(sig); // Codifica em Base64URL

        // Retorna true se a assinatura bater
        return expectedSig.equals(parts[2]);
    }
}
