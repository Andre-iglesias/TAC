package servidor;

// Importa classes auxiliares e utilitárias
import dao.UsuarioDAO;
import utils.hashUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

// Classe AuthAPI que implementa um handler HTTP para autenticação
public class AuthAPI implements HttpHandler {

    private final UsuarioDAO usuarioDAO; // DAO para acessar dados dos usuários no banco

    // Construtor recebe o DAO já conectado ao banco
    public AuthAPI(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    // Método que trata requisições HTTP
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Permite apenas o método POST
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            exchange.sendResponseHeaders(405, -1); // 405 Method Not Allowed
            return;
        }

        // Lê os dados do corpo da requisição (esperado: username=...&password=...)
        String formData = new BufferedReader(
                new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)
        ).readLine();

        // Converte os dados recebidos para um Map
        Map<String, String> params = parseForm(formData);
        String username = params.get("username");
        String password = params.get("password");

        // Verifica se os campos obrigatórios estão presentes
        if (username == null || password == null) {
            send(exchange, 400, "Missing username or password"); // 400 Bad Request
            return;
        }

        // Busca o usuário no banco usando o DAO
        Map<String, String> user = usuarioDAO.getUserByUsername(username);

        // Se não encontrou o usuário
        if (user == null) {
            send(exchange, 401, "Invalid credentials"); // 401 Unauthorized
            return;
        }

        // Recupera o salt e o hash esperado do banco
        String salt = user.get("salt");
        String expectedHash = user.get("senha_hash");

        // Gera o hash da senha recebida usando o mesmo salt
        String inputHash = hashUtil.hashSenhaComSalt(password, salt);

        // Compara o hash da senha fornecida com o hash armazenado
        if (!expectedHash.equals(inputHash)) {
            send(exchange, 401, "Invalid credentials"); // 401 Unauthorized
            return;
        }

        // Aqui poderia ser gerado um token JWT para autenticação contínua
        // Por enquanto, apenas retorna sucesso
        send(exchange, 200, "Login successful!");
    }

    // Método auxiliar para enviar respostas HTTP
    private void send(HttpExchange exchange, int code, String response) throws IOException {
        exchange.sendResponseHeaders(code, response.length()); // Envia o código HTTP
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes(StandardCharsets.UTF_8)); // Escreve a resposta
        os.close();
    }

    // Método auxiliar para parsear dados do tipo "username=...&password=..."
    private Map<String, String> parseForm(String formData) throws UnsupportedEncodingException {
        Map<String, String> map = new HashMap<>();
        if (formData == null) return map;

        for (String pair : formData.split("&")) {
            String[] parts = pair.split("=");
            if (parts.length == 2)
                map.put(URLDecoder.decode(parts[0], "UTF-8"), URLDecoder.decode(parts[1], "UTF-8"));
        }
        return map;
    }
}
