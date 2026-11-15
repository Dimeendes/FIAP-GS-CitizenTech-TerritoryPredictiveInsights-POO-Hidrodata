package br.com.hidrodata.domain.model;

import java.util.Random;

import br.com.hidrodata.exception.FonteIndisponivelException;

public class SensorConsumo extends ComponenteTerritorial implements Sensor {
    private final Random random = new Random();

    public SensorConsumo(int id, String nome, String regiao, String status) {
        super(id, nome, regiao, status);
    }

    @Override
    public double lerValor() {
        if (!verificarFuncionamento()) {
            throw new FonteIndisponivelException("Sensor de consumo '" + getNome() + "' (id " + getId()
                    + ") está " + getStatus() + " e não pode ser lido.");
        }
        if (random.nextDouble() < 0.05) {
            throw new FonteIndisponivelException("Falha de comunicação com o sensor de consumo '"
                    + getNome() + "' (id " + getId() + ").");
        }
        return 800 + random.nextDouble() * 1200; // 800 a 2000 L/h
    }

    @Override
    public boolean verificarFuncionamento() {
        return "ATIVO".equalsIgnoreCase(getStatus());
    }

    @Override
    public String retornarTipo() {
        return "CONSUMO";
    }

    @Override
    public String verificarStatus() {
        return "Sensor de consumo '" + getNome() + "' (" + getRegiao() + "): " + getStatus();
    }
}