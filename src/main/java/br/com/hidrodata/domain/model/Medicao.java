package br.com.hidrodata.domain.model;

import br.com.hidrodata.exception.DadosInvalidosException;

public class Medicao extends ComponenteTerritorial implements Sensor {
    private double valorLitros;
    private String tipo;
    private String dataHora;

    public Medicao(int id, String nome, String regiao, String status, double valorLitros, String tipo, String dataHora) {
        super(id, nome, regiao, status);
        atualizarValorLitros(valorLitros);
        this.tipo = tipo;
        this.dataHora = dataHora;
    }

    @Override
    public double lerValor() {
        return valorLitros;
    }

    public void atualizarValorLitros(double novoValor) {
        if (novoValor < 0) {
            throw new DadosInvalidosException("O valor em litros não pode ser negativo.");
        }
        valorLitros = novoValor;
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
            throw new IllegalStateException("O status da medição não foi informado.");
        }
        return getStatus();
    }

    public String getDataHora() {
        return dataHora;
    }

    public void setDataHora(String dataHora) {
        this.dataHora = dataHora;
    }
}
