package servidor;

import dao.UsuarioDAO;
import utils.hashUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class AuthAPI implements HttpHandler {
    private final UsuarioDAO usuarioDAO;

    public AuthAPI(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        // Read POST data (username and password)
        String formData = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)).readLine();
        Map<String, String> params = parseForm(formData);
        String username = params.get("username");
        String password = params.get("password");

        if (username == null || password == null) {
            send(exchange, 400, "Missing username or password");
            return;
        }

        Map<String, String> user = usuarioDAO.getUserByUsername(username);

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

        // If you want to generate a JWT, add here.
        // For now, just send a success message.
        send(exchange, 200, "Login successful!");
    }

    private void send(HttpExchange exchange, int code, String response) throws IOException {
        exchange.sendResponseHeaders(code, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes(StandardCharsets.UTF_8));
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