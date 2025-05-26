package dao; // Define o pacote onde está localizada esta classe

import utils.hashUtil; // Importa a classe utilitária para hash de senhas
import java.sql.Connection; // Importa a classe para gerenciar a conexão com o banco de dados
import java.sql.PreparedStatement; // Permite usar comandos SQL com parâmetros (evita SQL Injection)
import java.sql.ResultSet; // Permite manipular o resultado de uma consulta SQL
import java.sql.SQLException; // Trata exceções relacionadas a banco de dados
import java.util.HashMap; // Estrutura de dados para armazenar pares chave-valor
import java.util.Map; // Interface de mapa (usada para retorno de dados do usuário)

public class UsuarioDAO {

    private Connection conexao; // Objeto de conexão com o banco de dados

    // Construtor que recebe uma conexão como parâmetro
    public UsuarioDAO(Connection conexao) {
        this.conexao = conexao;
    }

    // Método para salvar um novo usuário no banco de dados
    public void salvarUsuario(String username, String senha) {
        String salt = hashUtil.gerarSalt(); // Gera um salt aleatório
        String senhaHash = hashUtil.hashSenhaComSalt(senha, salt); // Gera o hash da senha com o salt

        String sql = "INSERT INTO usuarios (username, senha_hash, salt) VALUES (?, ?, ?)";

        // Tenta preparar e executar a instrução SQL
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, username);     // Define o valor do primeiro parâmetro
            stmt.setString(2, senhaHash);    // Define o segundo parâmetro (hash da senha)
            stmt.setString(3, salt);         // Define o terceiro parâmetro (salt)
            stmt.executeUpdate();            // Executa a inserção no banco
            System.out.println("Usuário salvo com sucesso!");
        } catch (SQLException e) {
            // Caso ocorra algum erro, lança uma exceção com uma mensagem personalizada
            throw new RuntimeException("Erro ao salvar usuário no banco", e);
        }
    }

    // Método para buscar o hash da senha e o salt de um usuário, usado para autenticação
    public Map<String, String> getUserByUsername(String username) {
        String sql = "SELECT senha_hash, salt FROM usuarios WHERE username = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, username); // Define o valor do parâmetro
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) { // Se encontrou o usuário
                    Map<String, String> result = new HashMap<>();
                    result.put("senha_hash", rs.getString("senha_hash")); // Recupera o hash da senha
                    result.put("salt", rs.getString("salt"));             // Recupera o salt
                    return result; // Retorna os dados em um mapa
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuário no banco", e);
        }
        return null; // Retorna null se o usuário não for encontrado
    }
}
