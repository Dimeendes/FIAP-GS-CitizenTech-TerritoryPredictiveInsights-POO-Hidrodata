package br.com.hidrodata.config;

import br.com.hidrodata.domain.model.DadosTerritorio;
import br.com.hidrodata.exception.DadosInvalidosException;

public class ConfiguracaoSistema {
    private static final ConfiguracaoSistema INSTANCIA = new ConfiguracaoSistema();

    private final DadosTerritorio territorio;
    private int janelaMediaMovel;
    private int tamanhoPeriodoComparacao;

    private ConfiguracaoSistema(){
        territorio = new DadosTerritorio(1, "Santana", "Zona Norte", "ATIVO", 3500   ,  150);
        
        territorio.alterarLimiar("CONSUMO", 1500, 1650, 1800); // L/h
        territorio.alterarLimiar("VAZAMENTO",  170, 210, 250);// L/h perdidos
        territorio.alterarLimiar("PRESSAO", 40, 45, 50); // mca

        janelaMediaMovel = 5;
        tamanhoPeriodoComparacao = 3; 
    
    }

    public static ConfiguracaoSistema getInstancia(){
        return INSTANCIA;
    }

    public DadosTerritorio getTerritorio() {
        return territorio;
    }

    public int getJanelaMediaMovel() {
        return janelaMediaMovel;
    }

    public void alterarJanelaMediaMovel(int novaJanela) {
        if (novaJanela < 2 || novaJanela > 50) {
            throw new DadosInvalidosException("A janela da média móvel deve ser entre 2 e 50 (Recebido: " + novaJanela + "  )");
        }
        janelaMediaMovel = novaJanela;
    }

    public int getTamanhoPeriodoComparacao() {
        return tamanhoPeriodoComparacao;
    }

    public void alterarTamanhoPeriodoComparacao(int novoTamanho) {
        if (novoTamanho < 1 || novoTamanho > 25) {
            throw new DadosInvalidosException("O tamanho do período de comparação deve ser entre 1 e 25 (Recebido: " + novoTamanho + "  )");
        }
        tamanhoPeriodoComparacao = novoTamanho;
    }


}
