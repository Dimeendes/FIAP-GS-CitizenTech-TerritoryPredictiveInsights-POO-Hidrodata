package br.com.hidrodata.service;

import br.com.hidrodata.domain.model.Medicao;
import br.com.hidrodata.domain.model.Sensor;

public record ResultadoLeitura(Sensor sensor, Medicao medicao, String erro) {
    
    public static ResultadoLeitura sucesso(Sensor sensor, Medicao medicao){
        return new ResultadoLeitura(sensor, medicao, null);
    }

    public static ResultadoLeitura falha(Sensor sensor, String erro){
        return new ResultadoLeitura(sensor, null, erro);
    }

    public boolean foiLido() {
        return medicao != null;
    }
}
