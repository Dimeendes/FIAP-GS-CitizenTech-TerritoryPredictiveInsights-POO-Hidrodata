package br.com.hidrodata.domain.model;

import java.util.Random;

import br.com.hidrodata.exception.FonteIndisponivelException;

public class SensorPressao extends ComponenteTerritorial implements Sensor {
    private final Random random = new Random();

    public SensorPressao(int id, String nome, String regiao, String status) {
        super(id, nome, regiao, status);
    }

    @Override
    public double lerValor() {
        if (!verificarFuncionamento()) {
            throw new FonteIndisponivelException("Sensor de pressão '" + getNome() + "' (id " + getId()
                    + ") está " + getStatus() + " e não pode ser lido.");
        }
        if (random.nextDouble() < 0.05) {
            throw new FonteIndisponivelException("Falha de comunicação com o sensor de pressão '"
                    + getNome() + "' (id " + getId() + ").");
        }
        return 10 + random.nextDouble() * 50; // 10 a 60 mca (metros de coluna d'água)
    }

    @Override
    public boolean verificarFuncionamento() {
        return "ATIVO".equalsIgnoreCase(getStatus());
    }

    @Override
    public String retornarTipo() {
        return "PRESSAO";
    }

    @Override
    public String verificarStatus() {
        return "Sensor de pressão '" + getNome() + "' (" + getRegiao() + "): " + getStatus();
    }
}