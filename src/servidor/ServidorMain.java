package servidor;

import com.sun.net.httpserver.HttpServer;
import dao.UsuarioDAO;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;

public class ServidorMain {
    public static void main(String[] args) throws Exception {
        Connection conexao = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/autenticacao", "root", "qwerty12"
        );
        UsuarioDAO dao = new UsuarioDAO(conexao);

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/login", new AuthAPI(dao));
        server.createContext("/protected", new ProtectedAPI());
        server.setExecutor(null);
        server.start();
        System.out.println("Server started at http://localhost:8080");
    }
}