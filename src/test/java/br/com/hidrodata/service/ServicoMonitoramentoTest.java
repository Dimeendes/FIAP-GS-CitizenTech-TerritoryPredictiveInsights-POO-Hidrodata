package br.com.hidrodata.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.hidrodata.domain.model.Medicao;
import br.com.hidrodata.domain.model.Sensor;
import br.com.hidrodata.domain.model.SensorConsumo;
import br.com.hidrodata.domain.strategy.PrevisaoMediaMovel;
import br.com.hidrodata.exception.DadosInvalidosException;
import br.com.hidrodata.repository.AlertaDao;
import br.com.hidrodata.repository.MedicaoDao;

@ExtendWith(MockitoExtension.class)
public class ServicoMonitoramentoTest {

    @Mock
    private MedicaoDao medicaoDao;

    @Mock
    private AlertaDao alertaDao;

    @Mock
    private FabricaPrevisao fabricaPrevisao;

    @InjectMocks
    private ServicoMonitoramento servico;

    @Test
    void deveDisponibilizarSensoresPadrao() {
        List<Integer> ids = servico.listarSensores().stream()
                .map(Sensor::getId)
                .toList();

        assertEquals(List.of(1, 2, 3, 4), ids);
    }

    @Test
    void deveLancarExcecaoQuandoSensorForNulo() {
        DadosInvalidosException exception = assertThrows(
                DadosInvalidosException.class,
                () -> servico.adicionarSensor(null));

        assertEquals("Sensor não pode ser nulo.", exception.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoIdDoSensorEstiverDuplicado() {
        Sensor sensorDuplicado = new SensorConsumo(
                1, "Hidrômetro duplicado", "Zona Norte", "ATIVO");

        DadosInvalidosException exception = assertThrows(
                DadosInvalidosException.class,
                () -> servico.adicionarSensor(sensorDuplicado));

        assertEquals("Sensor com ID 1 já está cadastrado.", exception.getMessage());
    }

    @Test
    void deveBuscarHistoricoDeSensorExistente() {
        List<Medicao> historico = List.of();
        when(medicaoDao.buscarHistoricoPorSensor(1)).thenReturn(historico);

        List<Medicao> resultado = servico.buscarHistoricoSensor(1);

        assertEquals(historico, resultado);
        verify(medicaoDao).buscarHistoricoPorSensor(1);
    }

    @Test
    void deveLancarExcecaoAntesDeBuscarHistoricoDeSensorInexistente() {
        DadosInvalidosException exception = assertThrows(
                DadosInvalidosException.class,
                () -> servico.buscarHistoricoSensor(99));

        assertEquals("Sensor com ID 99 não encontrado.", exception.getMessage());
        verifyNoInteractions(medicaoDao);
    }

    @Test
    void deveRetornarSemPrevisaoQuandoHistoricoForInsuficiente() {
        Sensor sensor = servico.buscarSensorPorId(1);
        PrevisaoMediaMovel estrategia = new PrevisaoMediaMovel(2);
        when(medicaoDao.buscarUltimasPorSensor(1, 2)).thenReturn(List.of());

        ResultadoPrevisao resultado = servico.preverParaSensor(sensor, estrategia);

        assertFalse(resultado.foiCalculada());
        assertEquals(sensor, resultado.sensor());
        assertEquals(estrategia.getNomeEstrategia(), resultado.estrategia());
        assertTrue(resultado.aviso().contains("pelo menos 2 leituras"));
        verify(medicaoDao).buscarUltimasPorSensor(1, 2);
        verifyNoInteractions(alertaDao);
    }
}
