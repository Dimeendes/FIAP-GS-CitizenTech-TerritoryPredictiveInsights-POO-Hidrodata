package br.com.hidrodata.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.com.hidrodata.domain.model.Medicao;
import br.com.hidrodata.exception.DadosInvalidosException;

public class MedicaoTest {
    private LocalDateTime dataHora;

    @BeforeEach
    void setUp() {
        dataHora = LocalDateTime.of(2026, 1, 2, 3, 4);
    }

    @Test
    void deveCriarMedicaoSemId() {
        Medicao medicao = new Medicao(7, "CONSUMO", 12.5, dataHora);

        assertNull(medicao.getId());
        assertEquals(7, medicao.getSensorId());
        assertEquals("CONSUMO", medicao.getTipoSensor());
        assertEquals(12.5, medicao.getValor());
        assertEquals(dataHora, medicao.getDataHora());
    }

    @Test
    void deveCriarMedicaoComId() {
        Medicao medicao = new Medicao(15L, 7, "CONSUMO", 12.5, dataHora);

        assertEquals(15L, medicao.getId());
    }

    @Test
    void deveLancarExcecaoQuandoTipoSensorNulo() {
        DadosInvalidosException exception = assertThrows(
                DadosInvalidosException.class,
                () -> new Medicao(7, null, 12.5, dataHora));

        assertEquals("O tipo do sensor não pode ser nulo ou vazio.", exception.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoTipoSensorEstiverEmBranco() {
        DadosInvalidosException exception = assertThrows(
                DadosInvalidosException.class,
                () -> new Medicao(7, "  ", 12.5, dataHora));

        assertEquals("O tipo do sensor não pode ser nulo ou vazio.", exception.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoValorForNegativo() {
        DadosInvalidosException exception = assertThrows(
                DadosInvalidosException.class,
                () -> new Medicao(7, "CONSUMO", -1, dataHora));

        assertEquals(
                "Medição inválida do sensor 7: valor -1.0 fora de faixa.",
                exception.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoValorForNaN() {
        DadosInvalidosException exception = assertThrows(
                DadosInvalidosException.class,
                () -> new Medicao(7, "CONSUMO", Double.NaN, dataHora));

        assertEquals(
                "Medição inválida do sensor 7: valor NaN fora de faixa.",
                exception.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoValorForInfinito() {
        DadosInvalidosException exception = assertThrows(
                DadosInvalidosException.class,
                () -> new Medicao(7, "CONSUMO", Double.POSITIVE_INFINITY, dataHora));

        assertEquals(
                "Medição inválida do sensor 7: valor Infinity fora de faixa.",
                exception.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoDataHoraForNula() {
        DadosInvalidosException exception = assertThrows(
                DadosInvalidosException.class,
                () -> new Medicao(7, "CONSUMO", 12.5, null));

        assertEquals("A data e hora da medição não pode ser nula.", exception.getMessage());
    }
}
