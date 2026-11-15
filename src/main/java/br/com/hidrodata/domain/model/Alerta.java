package br.com.hidrodata.domain.model;

import java.time.LocalDateTime;

import br.com.hidrodata.exception.DadosInvalidosException;

public class Alerta {
    private Long id;
    private int sensorId;
    private String tipoSensor;
    private NivelAlerta nivel;
    private double valorPrevisto;
    private double limite;
    private String estrategia;
    private String mensagem;
    private LocalDateTime dataHora;

    public Alerta(int sensorId, String tipoSensor, NivelAlerta nivel, double valorPrevisto, double limite, String estrategia, String mensagem, LocalDateTime dataHora) {
        this(null, sensorId, tipoSensor, nivel, valorPrevisto, limite, estrategia, mensagem, dataHora);
    }

    public Alerta(Long id, int sensorId, String tipoSensor, NivelAlerta nivel, double valorPrevisto, double limite, String estrategia, String mensagem, LocalDateTime dataHora) {
        if(nivel == null) {
            throw new DadosInvalidosException("O nível do alerta não pode ser nulo.");
        }
        if(mensagem == null || mensagem.isBlank()) {
            throw new DadosInvalidosException("A mensagem do alerta não pode ser nula ou vazia.");
        }
        if(dataHora == null) {
            throw new DadosInvalidosException("A data e hora da medição não pode ser nula.");
        }
        this.id = id;  
        this.sensorId = sensorId;
        this.tipoSensor = tipoSensor;
        this.nivel = nivel;
        this.valorPrevisto = valorPrevisto;
        this.limite = limite;
        this.estrategia = estrategia;
        this.mensagem = mensagem;
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

    public NivelAlerta getNivel() {
        return nivel;
    }

    public double getValorPrevisto() {
        return valorPrevisto;
    }

    public double getLimite() {
        return limite;
    }

    public String getEstrategia() {
        return estrategia;
    }

    public String getMensagem() {
        return mensagem;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }
}