package servidor;

import dao.UsuarioDAO;
import utils.hashUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.*;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;

public class AuthAPI implements HttpHandler {
    private UsuarioDAO usuarioDAO;

    public AuthAPI(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            return;
        }
        // Parse form
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String formData = br.readLine();
        Map<String, String> params = parseForm(formData);

        String username = params.get("username");
        String password = params.get("password");

        // Simple: get salt & hash from DB, hash input password+salt and compare
        try {
            Connection conexao = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/autenticacao", "root", "qwerty12"
            );
            Map<String, String> user = usuarioDAO.getUserByUsername(conexao, username);
            if (user == null) {
                send(exchange, 401, "Invalid credentials");
                return;
            }
            String salt = user.get("salt");
            String expectedHash = user.get("senha_hash");
            String inputHash = hashUtil.hashSenhaComSalt(password, salt);

            if (!expectedHash.equals(inputHash)) {
                send(exchange, 401, "Invalid credentials");
                return;
            }
            // Generate JWT (stubbed)
            String jwt = JWTHmac.createJWT("{\"user\":\"" + username + "\"}", "your-secret");
            send(exchange, 200, jwt);
        } catch (Exception e) {
            send(exchange, 500, "Server error: " + e.getMessage());
        }
    }

    private void send(HttpExchange exchange, int code, String response) throws IOException {
        exchange.sendResponseHeaders(code, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

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