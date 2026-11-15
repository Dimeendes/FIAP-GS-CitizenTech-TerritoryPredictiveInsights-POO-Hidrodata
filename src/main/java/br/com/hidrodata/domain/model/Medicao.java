package br.com.hidrodata.domain.model;

import java.time.LocalDateTime;

import br.com.hidrodata.exception.DadosInvalidosException;

public class Medicao{
    private Long id;
    private int sensorId;
    private String tipoSensor;
    private double valor;
    private LocalDateTime dataHora;

    public Medicao(int sensorId, String tipoSensor, double valor, LocalDateTime dataHora) {
        this(null, sensorId, tipoSensor, valor, dataHora);
    }

    public Medicao(Long id, int sensorId, String tipoSensor, double valor, LocalDateTime dataHora) {
        if(tipoSensor == null || tipoSensor.isBlank()) {
            throw new DadosInvalidosException("O tipo do sensor não pode ser nulo ou vazio.");
        }
        if(Double.isNaN(valor) || Double.isInfinite(valor) || valor < 0) {
            throw new DadosInvalidosException("Medição inválida do sensor " + sensorId + ": valor " + valor + " fora de faixa.");
        }
        if(dataHora == null) {
            throw new DadosInvalidosException("A data e hora da medição não pode ser nula.");
        }
        this.id = id;  
        this.sensorId = sensorId;
        this.tipoSensor = tipoSensor;
        this.valor = valor;
        this.dataHora = dataHora;
    }
    
    public Long getId() {
        return id;
    }

    public int getSensorId() {
        return sensorId;
    }

    public String getTipoSensor() {
        return tipoSensor;
    }

    public double getValor() {
        return valor;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }
}