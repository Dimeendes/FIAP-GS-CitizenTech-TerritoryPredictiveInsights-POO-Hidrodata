package br.com.hidrodata.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import br.com.hidrodata.config.ConfiguracaoSistema;
import br.com.hidrodata.domain.strategy.EstrategiaPrevisao;
import br.com.hidrodata.domain.strategy.PrevisaoComparacao;
import br.com.hidrodata.domain.strategy.PrevisaoMediaMovel;
import br.com.hidrodata.exception.DadosInvalidosException;

@Component 
public class FabricaPrevisao {
    private final Map<String, Function<ConfiguracaoSistema, EstrategiaPrevisao>> criadores = new LinkedHashMap<>();
    
    public FabricaPrevisao() {
        criadores.put("MEDIA_MOVEL", config -> new PrevisaoMediaMovel(config.getJanelaMediaMovel()));
        criadores.put("COMPARACAO_PERIODOS", config -> new PrevisaoComparacao(config.getTamanhoPeriodoComparacao()));
    }

    public EstrategiaPrevisao criarEstrategia(String codigo) {
        Function<ConfiguracaoSistema, EstrategiaPrevisao> criador = criadores.get(codigo == null ?  "" : codigo.trim().toUpperCase());
        if (criador == null) {
            throw new DadosInvalidosException("Estratégia de previsão desconhecida: " + codigo + ". Opções válidas: " + criadores.keySet() + ".");
        }
        return criador.apply(ConfiguracaoSistema.getInstancia());
    }

    public List<EstrategiaPrevisao> criarTodasEstrategias() {
        List<EstrategiaPrevisao> estrategias = new ArrayList<>();
        for (String codigo : criadores.keySet()) {
            estrategias.add(criarEstrategia(codigo));
        }
        return estrategias;
    }

    public List<String> listarCodigosEstrategias() {
        return List.copyOf(criadores.keySet());
    }

}
