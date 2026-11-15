package br.com.hidrodata.domain.model;

import java.util.HashMap;
import java.util.Map;

import br.com.hidrodata.exception.DadosInvalidosException;

public class DadosTerritorio extends ComponenteTerritorial {
    private int populacaoTerritorio;
    private double gastoMedioLitros;
    private final Map<String, Limiar> limiares = new HashMap<>();

    public DadosTerritorio(int id, String nome, String regiao, String status, int populacaoTerritorio, double gastoMedioLitros) {
        super(id, nome, regiao, status);
        atualizarPopulacaoTerritorio(populacaoTerritorio);   // antes: setPopulacaoTerritorio(...)
        atualizarGastoMedioLitros(gastoMedioLitros);         // antes: setGastoMedioLitros(...)
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

    public void alterarLimiar(String tipoSensor, double atencao, double alerta, double critico) {
        if (tipoSensor == null || tipoSensor.isBlank()) {
            throw new DadosInvalidosException("Informe o tipo de sensor do limiar.");
        }
        limiares.put(tipoSensor.toUpperCase(), new Limiar(atencao, alerta, critico));
    }

    public Limiar buscarLimiar(String tipoSensor) {
        Limiar limiar = limiares.get(tipoSensor == null ? "" : tipoSensor.toUpperCase());
        if (limiar == null) {
            throw new DadosInvalidosException("Não há limiar configurado para o sensor do tipo " + tipoSensor + ".");
        }
        return limiar;
    }

    public Map<String, Limiar> listarLimiares() {
        return Map.copyOf(limiares);
    }

    @Override
    public String verificarStatus() {
        return "Território '" + getNome() + "' (" + getRegiao() + "): " + getStatus() + " | população: " + populacaoTerritorio + " | gasto médio: " + gastoMedioLitros + " L/dia por pessoa" + " | limiares configurados: " + limiares.size();
    }
}