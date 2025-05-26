package servidor;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

// Classe que representa uma rota protegida por autenticação JWT
public class ProtectedAPI implements HttpHandler {

    // Método chamado para lidar com requisições HTTP recebidas
    @Override
    public void handle(HttpExchange exchange) throws IOException {

        // Obtém o valor do cabeçalho Authorization
        // Esperado: "Authorization: Bearer <token_jwt>"
        String auth = exchange.getRequestHeaders().getFirst("Authorization");

        // Se o cabeçalho estiver ausente ou malformado, retorna 401 (Unauthorized)
        if (auth == null || !auth.startsWith("Bearer ")) {
            exchange.sendResponseHeaders(401, -1); // Não autorizado
            return;
        }

        // Extrai apenas o token JWT da string "Bearer <token>"
        String jwt = auth.substring("Bearer ".length());

        try {
            // Valida o token usando a chave secreta
            boolean valid = JWTHmac.validateJWT(jwt, "your-secret"); // ⚠️ Substituir "your-secret" por uma chave segura real

            // Se o token for inválido, responde com 401
            if (!valid) {
                exchange.sendResponseHeaders(401, -1);
                return;
            }

            // Se o token for válido, envia a resposta da rota protegida
            String response = "Access granted to protected resource!";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();

        } catch (Exception e) {
            // Em caso de erro interno, responde com 500 (Internal Server Error)
            exchange.sendResponseHeaders(500, -1);
        }
    }
}
