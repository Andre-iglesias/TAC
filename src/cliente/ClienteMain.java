package cliente;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ClienteMain {
    public static void main(String[] args) throws Exception {
        // 1. Login (use port 8080, not 3306)
        String loginUrl = "http://localhost:8080/login";
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
        String response = new BufferedReader(new InputStreamReader(conn.getInputStream())).readLine();
        System.out.println("Server response: " + response);
    }
}