package br.com.hidrodata.domain.model;

public class SensorData {
    private String id;
    private double leituraSensorLitros;

    public SensorData(String id, double leituraSensorLitros) {
        this.id = id;
        setLeituraSensorLitros(leituraSensorLitros);
    }

    public String getId() {
        return id;
    }

    public double getLeituraSenorLitros() {
        return leituraSensorLitros;
    }

    private void setLeituraSensorLitros(double leituraNova) {
        this.leituraSensorLitros = leituraNova;
    }

    public void AlterarLeituraSensorLitros(double leituraNova) {
        leituraNova = (double) (Math.random() * 10);
    }
}