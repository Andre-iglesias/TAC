package dao;
import utils.hashUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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

}
