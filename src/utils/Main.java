package utils;

import dao.UsuarioDAO;
import servidor.JWTHmac;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;

public class Main extends JFrame {
    private JTextField regUserField;
    private JPasswordField regPassField;
    private JButton regBtn;

    private JTextField loginUserField;
    private JPasswordField loginPassField;
    private JButton loginBtn;

    private UsuarioDAO dao;
    private static final String SECRET = "your-secret"; // Use your real secret

    public Main() {
        setTitle("Auth System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);

        try {
            Connection conexao = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/autenticacao", "root", "qwerty12"
            );
            dao = new UsuarioDAO(conexao);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao conectar ao banco: " + e.getMessage());
            System.exit(1);
        }

        JTabbedPane tabs = new JTabbedPane();

        // Register Panel
        JPanel regPanel = new JPanel(new GridLayout(3, 2));
        regUserField = new JTextField();
        regPassField = new JPasswordField();
        regBtn = new JButton("Registrar");

        regPanel.add(new JLabel("Novo Usuário:"));
        regPanel.add(regUserField);
        regPanel.add(new JLabel("Nova Senha:"));
        regPanel.add(regPassField);
        regPanel.add(new JLabel(""));
        regPanel.add(regBtn);

        regBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = regUserField.getText();
                String senha = new String(regPassField.getPassword());
                try {
                    dao.salvarUsuario(username, senha);
                    JOptionPane.showMessageDialog(Main.this, "Usuário registrado com sucesso!");
                    regUserField.setText("");
                    regPassField.setText("");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(Main.this, "Erro: " + ex.getMessage());
                }
            }
        });

        // Login Panel
        JPanel loginPanel = new JPanel(new GridLayout(3, 2));
        loginUserField = new JTextField();
        loginPassField = new JPasswordField();
        loginBtn = new JButton("Login");

        loginPanel.add(new JLabel("Usuário:"));
        loginPanel.add(loginUserField);
        loginPanel.add(new JLabel("Senha:"));
        loginPanel.add(loginPassField);
        loginPanel.add(new JLabel(""));
        loginPanel.add(loginBtn);

        loginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = loginUserField.getText();
                String senha = new String(loginPassField.getPassword());
                try {
                    Map<String, String> user = dao.getUserByUsername(username);
                    if (user == null) {
                        JOptionPane.showMessageDialog(Main.this, "Usuário não encontrado!");
                        return;
                    }
                    String salt = user.get("salt");
                    String expectedHash = user.get("senha_hash");
                    String inputHash = utils.hashUtil.hashSenhaComSalt(senha, salt);

                    if (!expectedHash.equals(inputHash)) {
                        JOptionPane.showMessageDialog(Main.this, "Senha incorreta!");
                    } else {
                        // --- ADDED: JWT Creation, printing, and verification ---
                        String ticketid = username;
                        String payloadJson = "{\"ticketid\":\"" + ticketid + "\"}";
                        String jwt = JWTHmac.createJWT(payloadJson, SECRET);

                        // Print ticketid and JWT to console
                        System.out.println("ticketid: " + ticketid);
                        System.out.println("JWT: " + jwt);

                        // Prove HMAC signature
                        boolean valid = JWTHmac.validateJWT(jwt, SECRET);
                        System.out.println("JWT signature valid? " + valid);

                        JOptionPane.showMessageDialog(Main.this, "OI, Lorena");
                        loginUserField.setText("");
                        loginPassField.setText("");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(Main.this, "Erro: " + ex.getMessage());
                }
            }
        });

        tabs.addTab("Registrar", regPanel);
        tabs.addTab("Login", loginPanel);

        add(tabs);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Main().setVisible(true);
        });
    }
}