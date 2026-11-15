package br.com.hidrodata.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.stereotype.Repository;

import br.com.hidrodata.domain.model.Alerta;
import br.com.hidrodata.domain.model.NivelAlerta;
import br.com.hidrodata.exception.FonteIndisponivelException;

@Repository
public class AlertaDao {
    private static final String SQL_INSERIR = "INSERT INTO alerta(sensor_id, tipo_sensor, nivel, valor_previsto, limite, estrategia, mensagem, data_hora) " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_LISTAR_TODOS = "SELECT id, sensor_id, tipo_sensor, nivel, valor_previsto, limite, estrategia, mensagem, data_hora " + "FROM alerta ORDER BY data_hora DESC";
    private final DataSource dataSource;
    
    public AlertaDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Alerta salvar(Alerta alerta) {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement comando = conexao.prepareStatement(SQL_INSERIR, Statement.RETURN_GENERATED_KEYS)) {

            comando.setInt(1, alerta.getSensorId());
            comando.setString(2, alerta.getTipoSensor());
            comando.setString(3, alerta.getNivel().name());
            comando.setDouble(4, alerta.getValorPrevisto());
            comando.setDouble(5, alerta.getLimite());
            comando.setString(6, alerta.getEstrategia());
            comando.setString(7, alerta.getMensagem());
            comando.setTimestamp(8, Timestamp.valueOf(alerta.getDataHora()));
            comando.executeUpdate();

            try (ResultSet chaves = comando.getGeneratedKeys()) {
                chaves.next();
                long idGerado = chaves.getLong(1);
                return new Alerta(idGerado, alerta.getSensorId(), alerta.getTipoSensor(), alerta.getNivel(),
                        alerta.getValorPrevisto(), alerta.getLimite(), alerta.getEstrategia(),
                        alerta.getMensagem(), alerta.getDataHora());
            }
        } catch (SQLException e) {
            throw new FonteIndisponivelException("Não foi possível gravar o alerta do sensor " + alerta.getSensorId() + " no banco.", e);
        }
    }

    public List<Alerta> listarTodos() {
        try(Connection conexao = dataSource.getConnection();
             PreparedStatement comando = conexao.prepareStatement(SQL_LISTAR_TODOS);
             ResultSet resultado = comando.executeQuery()) {

            List<Alerta> alertas = new ArrayList<>();
                while (resultado.next()) {
                    alertas.add(new Alerta(
                            resultado.getLong("id"),
                            resultado.getInt("sensor_id"),
                            resultado.getString("tipo_sensor"),
                            NivelAlerta.valueOf(resultado.getString("nivel")),
                            resultado.getDouble("valor_previsto"),
                            resultado.getDouble("limite"),
                            resultado.getString("estrategia"),
                            resultado.getString("mensagem"),
                            resultado.getTimestamp("data_hora").toLocalDateTime()
                    ));
                }return alertas;
            }catch(SQLException e) {
                throw new FonteIndisponivelException("Não foi possível consultar os alertas no banco.", e);
        }
    }
}
  