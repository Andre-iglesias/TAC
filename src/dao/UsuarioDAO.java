package dao;
import utils.hashUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class UsuarioDAO {

    private Connection conexao;

    public UsuarioDAO(Connection conexao) {
        this.conexao = conexao;
    }

    public void salvarUsuario(String username, String senha) {
        String salt = hashUtil.gerarSalt();
        String senhaHash = hashUtil.hashSenhaComSalt(senha, salt);

        String sql = "INSERT INTO usuarios (username, senha_hash, salt) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, senhaHash);
            stmt.setString(3, salt);
            stmt.executeUpdate();
            System.out.println("Usuário salvo com sucesso!");
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar usuário no banco", e);
        }
    }

    // Add this method for authentication!
    public Map<String, String> getUserByUsername(String username) {
        String sql = "SELECT senha_hash, salt FROM usuarios WHERE username = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Map<String, String> result = new HashMap<>();
                    result.put("senha_hash", rs.getString("senha_hash"));
                    result.put("salt", rs.getString("salt"));
                    return result;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuário no banco", e);
        }
        return null;
    }
}