package br.com.hidrodata.domain.strategy;

import br.com.hidrodata.domain.model.Medicao;
import java.util.List;

public class PrevisaoComparacao extends EstrategiaPrevisao{
    private final int tamanhoPeriodo;

    public PrevisaoComparacao(int tamanhoPeriodo) {
        super("Comparação de Períodos", tamanhoPeriodo * 2);
        this.tamanhoPeriodo = tamanhoPeriodo;
    }

    @Override 
    public double prever(List<Medicao> historico) {
        validarHistorico(historico);
        double mediaAnterior = super.prever(periodoAnterior(historico));
        double mediaAtual = super.prever(periodoAtual(historico));
        double variacao = mediaAtual - mediaAnterior;
        return Math.max(0, mediaAtual + variacao); // Garante que o valor previsto não seja negativo
    }

    @Override
    public String explicarPrevisao(List<Medicao> historico, double valorPrevisto) {
        double mediaAnterior = super.prever(periodoAnterior(historico));
        double mediaAtual = super.prever(periodoAtual(historico));
        double variacao = mediaAtual - mediaAnterior;
        
        String tendencia;
        if(variacao > 0) {
            tendencia = "aumentando";
        } else if(variacao < 0) {
            tendencia = "diminuindo";
        } else {
            tendencia = "estável";
        }
        return String.format("Média das últimas %d leituras (%.2f) %s %.2f em relação às %d anteriores (%.2f). Se a tendência se mantiver, a próxima leitura deve ficar perto de %.2f.", 
                tamanhoPeriodo, mediaAtual, tendencia, Math.abs(variacao), tamanhoPeriodo, mediaAnterior, valorPrevisto);
        }
        private List<Medicao> periodoAtual(List<Medicao> historico){
            int fim = historico.size();
            return historico.subList(fim - tamanhoPeriodo, fim);
        }

        private List<Medicao> periodoAnterior(List<Medicao> historico){
            int fim = historico.size() - tamanhoPeriodo;
            return historico.subList(fim - tamanhoPeriodo, fim);
        }

        public int getTamanhoPeriodo() {
            return tamanhoPeriodo;
        }
    }
