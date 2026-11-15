package br.com.hidrodata.domain.model;

public interface Sensor {
    double lerValor();

    boolean verificarFuncionamento();

    String retornarTipo();
}
