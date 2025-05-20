package utils;

import dao.UsuarioDAO;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;

public class Main {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Cadastro de Usuário");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);
        frame.setLayout(new GridLayout(4, 1));

        JTextField usernameField = new JTextField();
        JPasswordField senhaField = new JPasswordField();
        JButton salvarBtn = new JButton("Salvar");

        frame.add(new JLabel("Usuário:"));
        frame.add(usernameField);
        frame.add(new JLabel("Senha:"));
        frame.add(senhaField);
        frame.add(salvarBtn);

        salvarBtn.addActionListener(e -> {
            String username = usernameField.getText();
            String senha = new String(senhaField.getPassword());

            try {
                Connection conexao = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/autenticacao", "root", "qwerty12"
                );
                UsuarioDAO dao = new UsuarioDAO(conexao);
                dao.salvarUsuario(username, senha);
                JOptionPane.showMessageDialog(frame, "Usuário salvo com sucesso!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Erro: " + ex.getMessage());
            }
        });

        frame.setVisible(true);
    }
}