package br.com.hidrodata.domain.model;

public interface Sensor {
    double lerValor();

    boolean verificarFuncionamento();

    String retornarTipo();

    default double getValorLitros() {
        return lerValor();
    }

    default String getTipo() {
        return retornarTipo();
    }
}
