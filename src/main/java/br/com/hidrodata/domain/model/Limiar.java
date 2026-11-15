package br.com.hidrodata.domain.model;

import br.com.hidrodata.exception.DadosInvalidosException;

public class Limiar {
    private final double atencao;
    private final double alerta;
    private final double critico;

    public Limiar(double atencao, double alerta, double critico) {
        if (atencao <= 0 || alerta <= 0 || critico <= 0) {
            throw new DadosInvalidosException("Limiar inválido: os valores precisam ser maiores que zero.");
        }
        if (!(atencao < alerta && alerta < critico)) {
            throw new DadosInvalidosException("Limiar inválido: é preciso que ATENÇÃO < ALERTA < CRÍTICO (recebido "
                    + atencao + " / " + alerta + " / " + critico + ").");
        }
        this.atencao = atencao;
        this.alerta = alerta;
        this.critico = critico;
    }

    public double getAtencao() {
        return atencao;
    }

    public double getAlerta() {
        return alerta;
    }

    public double getCritico() {
        return critico;
    }
}