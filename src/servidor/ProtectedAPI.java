package servidor;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public class ProtectedAPI implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Get Authorization: Bearer <JWT>
        String auth = exchange.getRequestHeaders().getFirst("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            exchange.sendResponseHeaders(401, -1);
            return;
        }
        String jwt = auth.substring("Bearer ".length());
        try {
            boolean valid = JWTHmac.validateJWT(jwt, "your-secret");
            if (!valid) {
                exchange.sendResponseHeaders(401, -1);
                return;
            }
            String response = "Access granted to protected resource!";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        } catch (Exception e) {
            exchange.sendResponseHeaders(500, -1);
        }
    }
}