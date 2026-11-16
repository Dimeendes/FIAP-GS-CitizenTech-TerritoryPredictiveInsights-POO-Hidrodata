package br.com.hidrodata.domain.strategy;

import java.util.List;

import br.com.hidrodata.domain.model.Medicao;

public class PrevisaoMediaMovel extends EstrategiaPrevisao {
    private final int periodo;

    public PrevisaoMediaMovel(int periodo) {
        super("Média Móvel", periodo);
        this.periodo = periodo;
    }

    @Override 
    public double prever(List<Medicao> historico) {
        validarHistorico(historico);
       List<Medicao> ultimasLeituras = historico.subList(historico.size() - periodo, historico.size());
        return super.prever(ultimasLeituras);
    }

    @Override
    public String explicarPrevisao(List<Medicao> historico, double valorPrevisto) {
        return String.format("Média das últimas %d leituras: %.2f. Se o padrão recente continuar, a próxima leitura deve ficar perto desse valor.", periodo, valorPrevisto);
    }

    public int getPeriodo() {
        return periodo;
    }
}