package br.com.hidrodata.domain.model;

import br.com.hidrodata.exception.DadosInvalidosException;

public class DadosTerritorio extends ComponenteTerritorial {
    private int populacaoTerritorio;
    private double gastoMedioLitros;

    public DadosTerritorio(int id, String nome, String regiao, String status, int populacaoTerritorio, double gastoMedioLitros) {
        super(id, nome, regiao, status);
        setPopulacaoTerritorio(populacaoTerritorio);
        setGastoMedioLitros(gastoMedioLitros);
    }

    public int getPopulacaoTerritorio() {
        return populacaoTerritorio;
    }

    private void setPopulacaoTerritorio(int novaPopulacao) {
        this.populacaoTerritorio = novaPopulacao;
    }

    public double getGastoMedioLitros() {
        return gastoMedioLitros;
    }

    private void setGastoMedioLitros(double novoGasto) {
        this.gastoMedioLitros = novoGasto;
    }

    public void atualizarPopulacaoTerritorio(int novaPopulacao) {
        if (novaPopulacao < 0) {
            throw new DadosInvalidosException("A população do território não pode ser negativa.");
        } else {
            setPopulacaoTerritorio(novaPopulacao);
        }    
    }

    public void atualizarGastoMedioLitros(double novoGasto) {
        if (novoGasto < 0) {
            throw new DadosInvalidosException("O gasto médio em litros não pode ser negativo.");
        } else {
            setGastoMedioLitros(novoGasto);
        }
    }

    @Override
    public String verificarStatus() {
        if (getStatus() == null || getStatus().isBlank()) {
            throw new IllegalStateException("O status do território não foi informado.");
        }
        return getStatus();
    }
}
