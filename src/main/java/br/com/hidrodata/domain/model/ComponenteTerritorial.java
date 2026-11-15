package br.com.hidrodata.domain.model;

import br.com.hidrodata.exception.DadosInvalidosException;

public abstract class ComponenteTerritorial {
    private int id;
    private String nome;
    private String regiao;
    private String status;

    public ComponenteTerritorial(int id, String nome, String regiao, String status) {
        this.id = id;
        atualizarNome(nome);
        alterarRegiao(regiao);
        atualizarStatus(status);
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    private void setNome(String nome) {
        this.nome = nome;
    }

    public void atualizarNome(String novoNome) {
        if (novoNome == null || novoNome.trim().isEmpty()) {
            throw new DadosInvalidosException("O nome não pode ser nulo ou vazio.");
        } else {
            setNome(novoNome);
        }
    }

    public String getRegiao() {
        return regiao;
    }

    private void setRegiao(String novaRegiao) {
        this.regiao = novaRegiao;
        
    }

    public void alterarRegiao(String novaRegiao) {
        if (novaRegiao == null || novaRegiao.trim().isEmpty()) {
            throw new DadosInvalidosException("A região não pode ser nula ou vazia.");
        } else {
            setRegiao(novaRegiao);
        }
    }

    public String getStatus() {
        return status;
    }

    private void setStatus(String novoStatus) {
        this.status = novoStatus;
    }

    public void atualizarStatus(String novoStatus) {
        if (novoStatus == null || novoStatus.trim().isEmpty()) {
            throw new DadosInvalidosException("O status não pode ser nulo ou vazio.");
        } else {
            setStatus(novoStatus);
        }
    }

    public abstract String verificarStatus();
}
