package br.com.hidrodata.service;

import br.com.hidrodata.domain.model.Alerta;
import br.com.hidrodata.domain.model.Sensor;

public record ResultadoPrevisao(Sensor sensor, String estrategia, Double valorPrevisto, String explicacao, Alerta alerta, String aviso) {

    public static ResultadoPrevisao calculada(Sensor sensor, String estrategia, Double valorPrevisto, String explicacao, Alerta alerta){
        return new ResultadoPrevisao(sensor, estrategia, valorPrevisto, explicacao, alerta, null);
    }

    public static ResultadoPrevisao semPrevisao(Sensor sensor, String estrategia, String aviso){
        return new ResultadoPrevisao(sensor, estrategia,null, null, null, aviso);
    }

    public boolean foiCalculada() {
        return valorPrevisto != null;
    }

    public boolean gerouAlerta(){
        return alerta != null;
    }
}
