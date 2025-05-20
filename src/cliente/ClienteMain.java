package cliente;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ClienteMain {
    public static void main(String[] args) throws Exception {
        // 1. Login
        String loginUrl = "http://localhost:3306/login";
            String loginParams = "username=as&password=12"; // Use valid credentials you registered!
        HttpURLConnection conn = (HttpURLConnection) new URL(loginUrl).openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(loginParams.getBytes());
        }

        int code = conn.getResponseCode();
        if (code != 200) {
            System.out.println("Login failed!");
            return;
        }
        String jwt = new BufferedReader(new InputStreamReader(conn.getInputStream())).readLine();
        System.out.println("JWT: " + jwt);

        // 2. Access protected resource
        String protectedUrl = "http://localhost:8080/protected";
        HttpURLConnection conn2 = (HttpURLConnection) new URL(protectedUrl).openConnection();
        conn2.setRequestProperty("Authorization", "Bearer " + jwt);

        int code2 = conn2.getResponseCode();
        if (code2 == 200) {
            String response = new BufferedReader(new InputStreamReader(conn2.getInputStream())).readLine();
            System.out.println("Protected response: " + response);
        } else {
            System.out.println("Access denied, status: " + code2);
        }
    }
}