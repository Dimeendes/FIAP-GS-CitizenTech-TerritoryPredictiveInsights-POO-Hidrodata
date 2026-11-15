package br.com.hidrodata.domain.model;

public enum NivelAlerta{ 
    ATENCAO("ATENÇÃO"), ALERTA("ALERTA"), CRITICO("CRÍTICO");
    
    private final String descricao;
    
    NivelAlerta(String descricao){ 
        this.descricao = descricao; 
    }
    
    public String getDescricao(){ 
        return descricao; }
}
