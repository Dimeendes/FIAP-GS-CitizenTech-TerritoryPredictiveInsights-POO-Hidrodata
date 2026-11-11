package br.com.hidrodata.domain.model;

import br.com.hidrodata.exception.DadosInvalidosException;

public class Alerta extends ComponenteTerritorial implements Sensor {
    private double valorLitros;
    private String tipo;
    private String prioridade;

    public Alerta( String prioridade, int id, String nome, String regiao, String status, double valorLitros, String tipo ) {
        super(id, nome, regiao, status);
        this.valorLitros = valorLitros;
        this.tipo = tipo;
        this.prioridade = prioridade;
    }

    @Override
    public double lerValor() {
        return valorLitros;
    }

    private void setValorLitros(double novoValor) {
        this.valorLitros = novoValor;
    }

    public void atualizarValorLitros(double novoValor) {
        if (novoValor < 0) {
            throw new DadosInvalidosException("O valor em litros não pode ser negativo.");
        } else {
            setValorLitros(novoValor);
        }
    }

    @Override
    public boolean verificarFuncionamento() {
        return "ATIVO".equalsIgnoreCase(getStatus());
    }

    @Override
    public String retornarTipo() {
        return tipo;
    }

    @Override
    public String verificarStatus() {
        if (getStatus() == null || getStatus().isBlank()) {
            throw new IllegalStateException("O status do alerta não foi informado.");
        }
        return getStatus();
    }

    public String getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(String prioridade) {
        this.prioridade = prioridade;
    }
}
