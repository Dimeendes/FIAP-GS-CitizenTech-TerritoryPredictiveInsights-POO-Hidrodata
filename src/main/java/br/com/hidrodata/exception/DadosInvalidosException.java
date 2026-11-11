package br.com.hidrodata.exception;

public class DadosInvalidosException extends IllegalArgumentException {
    public DadosInvalidosException(String message) {
        super(message);
    }
}
