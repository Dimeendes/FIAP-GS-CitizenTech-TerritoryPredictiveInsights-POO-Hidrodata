package br.com.hidrodata.repository;

import br.com.hidrodata.domain.model.Medicao;
import br.com.hidrodata.exception.FonteIndisponivelException;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public class MedicaoDao {
    private static final String SQL_INSERIR = "INSERT INTO medicao (sensor_id, tipo_sensor, valor, data_hora) VALUES (?, ?, ?, ?)";
    private static final String SQL_HISTORICO_POR_SENSOR = "SELECT id, sensor_id, tipo_sensor, valor, data_hora FROM medicao " + "WHERE sensor_id = ? ORDER BY data_hora, id";
    private static final String SQL_ULTIMAS_POR_SENSOR = "SELECT id, sensor_id, tipo_sensor, valor, data_hora FROM medicao "  + "WHERE sensor_id = ? ORDER BY data_hora DESC, id DESC LIMIT ?";
    private static final String SQL_CONTAR_POR_SENSOR = "SELECT COUNT(*) FROM medicao WHERE sensor_id = ?";

    private final DataSource dataSource;

    public MedicaoDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Medicao salvar(Medicao medicao) {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement comando = conexao.prepareStatement(SQL_INSERIR, Statement.RETURN_GENERATED_KEYS)) {

            comando.setInt(1, medicao.getSensorId());
            comando.setString(2, medicao.getTipoSensor());
            comando.setDouble(3, medicao.getValor());
            comando.setTimestamp(4, Timestamp.valueOf(medicao.getDataHora()));
            comando.executeUpdate();

            try (ResultSet chaves = comando.getGeneratedKeys()) {
                chaves.next();
                long idGerado = chaves.getLong(1);
                return new Medicao(idGerado, medicao.getSensorId(), medicao.getTipoSensor(),
                        medicao.getValor(), medicao.getDataHora());
            }
        } catch (SQLException e) {
            throw new FonteIndisponivelException("Não foi possível gravar a medição do sensor " + medicao.getSensorId() + " no banco.", e);
        }
    }

    public List<Medicao> buscarHistoricoPorSensor(int sensorId) {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement comando = conexao.prepareStatement(SQL_HISTORICO_POR_SENSOR)) {

            comando.setInt(1, sensorId);
            return lerMedicoes(comando);
        } catch (SQLException e) {
            throw new FonteIndisponivelException("Não foi possível consultar o histórico do sensor " + sensorId + ".", e);
        }
    }

    public List<Medicao> buscarUltimasPorSensor(int sensorId, int quantidade) {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement comando = conexao.prepareStatement(SQL_ULTIMAS_POR_SENSOR)) {

            comando.setInt(1, sensorId);
            comando.setInt(2, quantidade);
            List<Medicao> ultimas = lerMedicoes(comando);
            Collections.reverse(ultimas); 
            return ultimas;
        } catch (SQLException e) {
            throw new FonteIndisponivelException("Não foi possível consultar as últimas medições do sensor " + sensorId + ".", e);
        }
    }

    public int contarPorSensor(int sensorId) {
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement comando = conexao.prepareStatement(SQL_CONTAR_POR_SENSOR)) {

            comando.setInt(1, sensorId);
            try (ResultSet resultado = comando.executeQuery()) {
                resultado.next();
                return resultado.getInt(1);
            }
        } catch (SQLException e) {
            throw new FonteIndisponivelException("Não foi possível contar as medições do sensor " + sensorId + ".", e);
        }
    }

    private List<Medicao> lerMedicoes(PreparedStatement comando) throws SQLException {
        List<Medicao> medicoes = new ArrayList<>();
        try (ResultSet resultado = comando.executeQuery()) {
            while (resultado.next()) {
                medicoes.add(new Medicao(
                        resultado.getLong("id"),
                        resultado.getInt("sensor_id"),
                        resultado.getString("tipo_sensor"),
                        resultado.getDouble("valor"),
                        resultado.getTimestamp("data_hora").toLocalDateTime()));
            }
        }
        return medicoes;
    }
}