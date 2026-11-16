package br.com.hidrodata.domain.strategy;

import java.util.List;

import br.com.hidrodata.domain.model.Medicao;
import br.com.hidrodata.exception.DadosInvalidosException;

public abstract class EstrategiaPrevisao {
    private final String nomeEstrategia;
    private final int minimoLeituras;

    protected EstrategiaPrevisao(String nomeEstrategia, int minimoLeituras) {
        if(nomeEstrategia == null || nomeEstrategia.isBlank()) {
            throw new DadosInvalidosException("O nome da estratégia não pode ser nulo ou vazio.");
        }
        if(minimoLeituras < 1) {
            throw new DadosInvalidosException("O mínimo de leituras deve ser pelo menos um.");
        }
        this.nomeEstrategia = nomeEstrategia;
        this.minimoLeituras = minimoLeituras;
    }

    public double prever(List<Medicao> historico) {
        if(historico == null || historico.isEmpty()) {
            throw new DadosInvalidosException("Não há medições disponíveis para realizar a previsão.");
        }
        double soma = 0;
        for(Medicao medicao : historico) {
            soma += medicao.getValor();
        }
        return soma/historico.size();
    }

    public abstract String explicarPrevisao(List<Medicao> historico, double valorPrevisto); // Explica em texto o que o valor previsto significa. 

    protected void validarHistorico(List<Medicao> historico) {
        int quantidade = (historico == null) ? 0 : historico.size();
        if(quantidade < minimoLeituras) {
            throw new DadosInvalidosException("O histórico de medições deve conter pelo menos " + minimoLeituras + " leituras para a estratégia " + nomeEstrategia + ". Quantidade atual: " + quantidade);
        }
    }

    public String getNomeEstrategia() {
        return nomeEstrategia;
    }

    public int getMinimoLeituras() {
        return minimoLeituras;
    }

}
