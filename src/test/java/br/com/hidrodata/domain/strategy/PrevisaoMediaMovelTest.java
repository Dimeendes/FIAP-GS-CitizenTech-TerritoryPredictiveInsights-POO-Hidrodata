package br.com.hidrodata.domain.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.com.hidrodata.domain.model.Medicao;
import br.com.hidrodata.exception.DadosInvalidosException;

public class PrevisaoMediaMovelTest {
    private PrevisaoMediaMovel estrategia;
    private List<Medicao> historico;

    @BeforeEach
    void setUp() {
        estrategia = new PrevisaoMediaMovel(2);
        historico = List.of(
                medicao(100),
                medicao(20),
                medicao(40));
    }

    @Test
    void deveCalcularMediaDasUltimasLeiturasDoPeriodo() {
        double resultado = estrategia.prever(historico);

        assertEquals(30.0, resultado, 0.001);
    }

    @Test
    void deveInformarNomeEPeriodoDaEstrategia() {
        assertEquals("Média Móvel", estrategia.getNomeEstrategia());
        assertEquals(2, estrategia.getPeriodo());
        assertEquals(2, estrategia.getMinimoLeituras());
    }

    @Test
    void deveExplicarPrevisaoComPeriodoEValorFormatados() {
        PrevisaoMediaMovel estrategia = new PrevisaoMediaMovel(3);

        assertEquals(
                String.format(
                        "Média das últimas 3 leituras: %.2f. Se o padrão recente continuar, a próxima leitura deve ficar perto desse valor.",
                        12.345),
                estrategia.explicarPrevisao(List.of(), 12.345));
    }

    @Test
    void deveRejeitarHistoricoComMenosLeiturasQueOPeriodo() {
        DadosInvalidosException exception = assertThrows(
                DadosInvalidosException.class,
                () -> estrategia.prever(List.of(medicao(10))));

        assertEquals(
                "O histórico de medições deve conter pelo menos 2 leituras para a estratégia Média Móvel. Quantidade atual: 1",
                exception.getMessage());
    }

    @Test
    void deveRejeitarHistoricoNulo() {
        DadosInvalidosException exception = assertThrows(
                DadosInvalidosException.class,
                () -> estrategia.prever(null));

        assertEquals(
                "O histórico de medições deve conter pelo menos 2 leituras para a estratégia Média Móvel. Quantidade atual: 0",
                exception.getMessage());
    }

    @Test
    void deveRejeitarPeriodoMenorQueUm() {
        DadosInvalidosException exception = assertThrows(
                DadosInvalidosException.class,
                () -> new PrevisaoMediaMovel(0));

        assertEquals("O mínimo de leituras deve ser pelo menos um.", exception.getMessage());
    }

    private Medicao medicao(double valor) {
        return new Medicao(1, "CONSUMO", valor, LocalDateTime.of(2026, 1, 1, 0, 0));
    }
}
