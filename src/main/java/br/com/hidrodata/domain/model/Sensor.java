package br.com.hidrodata.domain.model;

public interface Sensor {
    double lerValor();

    boolean verificarFuncionamento();

    String retornarTipo();

    int getId();

    String getNome();

    String getRegiao();

    String verificarStatus();

    void atualizarStatus(String novoStatus);
}
