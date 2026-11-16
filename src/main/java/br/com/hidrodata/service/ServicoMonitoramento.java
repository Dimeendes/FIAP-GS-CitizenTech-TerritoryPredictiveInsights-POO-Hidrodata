package br.com.hidrodata.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import br.com.hidrodata.config.ConfiguracaoSistema;
import br.com.hidrodata.domain.model.Alerta;
import br.com.hidrodata.domain.model.Limiar;
import br.com.hidrodata.domain.model.Medicao;
import br.com.hidrodata.domain.model.NivelAlerta;
import br.com.hidrodata.domain.model.Sensor;
import br.com.hidrodata.domain.model.SensorConsumo;
import br.com.hidrodata.domain.model.SensorPressao;
import br.com.hidrodata.domain.model.SensorVazamento;
import br.com.hidrodata.domain.strategy.EstrategiaPrevisao;
import br.com.hidrodata.exception.DadosInvalidosException;
import br.com.hidrodata.exception.FonteIndisponivelException;
import br.com.hidrodata.repository.AlertaDao;
import br.com.hidrodata.repository.MedicaoDao;

@Service 
public class ServicoMonitoramento {
    private static final Map<String, String> UNIDADES = Map.of(
        "CONSUMO", "L/h",
        "VAZAMENTO", "L/h",
        "PRESSAO", "mca");

    private static final Map<String, String> ACOES_SUGERIDAS = Map.of(
        "CONSUMO", "verificar uso anormal no setor e orientar os moradores sobre economia de água.",
        "VAZAMENTO", "enviar equipe para inspecionar a rede e isolar o trecho afetado.",
        "PRESSAO", "avisar a concessionária e verificar a válvula redutora de pressão para evitar rompimentos.");

    private final MedicaoDao medicaoDao;
    private final AlertaDao alertaDao;
    private final FabricaPrevisao fabricaPrevisao;
    private final Map<Integer, Sensor> sensores = new LinkedHashMap<>();
    
    public ServicoMonitoramento(MedicaoDao medicaoDao, AlertaDao alertaDao, FabricaPrevisao fabricaPrevisao) {
        this.medicaoDao = medicaoDao;
        this.alertaDao = alertaDao;
        this.fabricaPrevisao = fabricaPrevisao;
        cadastrarSensoresPadrao();
    }

    private void cadastrarSensoresPadrao() {
        adicionarSensor(new SensorConsumo(1, "Hidrômetro Setor A", "Zona Norte - Setor A", "ATIVO"));
        adicionarSensor(new SensorVazamento(2, "Detector Rede Principal", "Zona Norte - Rede principal", "ATIVO"));
        adicionarSensor(new SensorPressao(3, "Pressão Reservatório Alto", "Zona Norte - Reservatório Alto", "ATIVO"));
        adicionarSensor(new SensorConsumo(4, "Hidrômetro Setor B", "Zona Norte - Setor B", "MANUTENCAO"));
    }   

    public void adicionarSensor(Sensor sensor){
        if(sensor == null){
            throw new DadosInvalidosException("Sensor não pode ser nulo.");
        }
        if(sensores.containsKey(sensor.getId())){
            throw new DadosInvalidosException("Sensor com ID " + sensor.getId() + " já está cadastrado.");
        }
        sensores.put(sensor.getId(), sensor);
    }

    public List<Sensor> listarSensores() {
        return List.copyOf(sensores.values());
    }

    public Sensor buscarSensorPorId(int sensorId) {
        Sensor sensor = sensores.get(sensorId);
        if (sensor == null) {
            throw new DadosInvalidosException("Sensor com ID " + sensorId + " não encontrado.");
        }
        return sensor;
    }

    public void alterarStatusSensor(int sensorId, String novoStatus) {
        buscarSensorPorId(sensorId).atualizarStatus(novoStatus);
    }

    public List<ResultadoLeitura> verificarSensores(){
        List<ResultadoLeitura> resultados = new ArrayList<>();
        for(Sensor sensor : sensores.values()){
            try{
                double valor = sensor.lerValor();
                Medicao medicao = new Medicao(sensor.getId(), sensor.retornarTipo(), valor, LocalDateTime.now());
                resultados.add(ResultadoLeitura.sucesso(sensor, medicaoDao.salvar(medicao)));
            }catch(FonteIndisponivelException | DadosInvalidosException e){
                resultados.add(ResultadoLeitura.falha(sensor, e.getMessage()));
            }
        }
        return resultados;
    }

    public List<ResultadoPrevisao> gerarPrevisoes(){
        List<ResultadoPrevisao> resultados = new ArrayList<>();
        for(Sensor sensor : sensores.values()){
            for(EstrategiaPrevisao estrategia : fabricaPrevisao.criarTodasEstrategias()){
               resultados.add(preverParaSensor(sensor, estrategia));
            }
        }
        return resultados;
    }

    public ResultadoPrevisao preverParaSensor(Sensor sensor, EstrategiaPrevisao estrategia){
        List<Medicao> historico = medicaoDao.buscarUltimasPorSensor(sensor.getId(), estrategia.getMinimoLeituras());
        try{
            double valorPrevisto = estrategia.prever(historico);
            String explicacao = estrategia.explicarPrevisao(historico, valorPrevisto);
            Alerta alerta = avaliarAlerta(sensor, estrategia.getNomeEstrategia(), valorPrevisto);
            if(alerta != null){
                alertaDao.salvar(alerta);
            }
            return ResultadoPrevisao.calculada(sensor, estrategia.getNomeEstrategia(), valorPrevisto, explicacao, alerta);
        }catch(DadosInvalidosException e){
            return ResultadoPrevisao.semPrevisao(sensor, estrategia.getNomeEstrategia(), e.getMessage());
        }
    }

    public Alerta avaliarAlerta(Sensor sensor, String nomeEstrategia, double valorPrevisto){
        Limiar limiar = ConfiguracaoSistema.getInstancia().getTerritorio().buscarLimiar(sensor.retornarTipo());
        NivelAlerta nivel = classificar(limiar, valorPrevisto);
        if(nivel == null){
            return null;
        }
        double limite = limiteDoNivel(limiar, nivel);
        String unidade = unidadeDoTipo(sensor.retornarTipo());
        String mensagem = String.format(
            "[%s] %s (id %d, %s): a previsão por %s indica %.2f %s, acima do limite de %s (%.2f %s). Ação sugerida: %s",
                nivel.getDescricao(), sensor.getNome(), sensor.getId(), sensor.getRegiao(), nomeEstrategia,
                valorPrevisto, unidade, nivel.getDescricao(), limite, unidade,
                ACOES_SUGERIDAS.getOrDefault(sensor.retornarTipo(), "avaliar a situação no local."));

                return new Alerta(sensor.getId(), sensor.retornarTipo(), nivel, valorPrevisto, limite, nomeEstrategia, mensagem, LocalDateTime.now());

    }
    
    private NivelAlerta classificar(Limiar limiar, double valor){
        if(valor >= limiar.getCritico()){
            return NivelAlerta.CRITICO;
        }
        if(valor >= limiar.getAlerta()){
            return NivelAlerta.ALERTA;
        }
        if(valor >= limiar.getAtencao()){
            return NivelAlerta.ATENCAO;
        }
        return null;
    }

    private double limiteDoNivel(Limiar limiar, NivelAlerta nivel){
        return switch(nivel) {
            case CRITICO -> limiar.getCritico();
            case ALERTA -> limiar.getAlerta();
            case ATENCAO -> limiar.getAtencao();
       };
    }

    public String unidadeDoTipo(String tipoSensor){
        return UNIDADES.getOrDefault(tipoSensor, "");
    }

    public List<Medicao> buscarHistoricoSensor(int sensorId){
        buscarSensorPorId(sensorId);
        return medicaoDao.buscarHistoricoPorSensor(sensorId);
    }

    public List<Alerta> listarAlertas(){
        return alertaDao.listarTodos();
    }

    public String gerarStatusCompleto(){
        ConfiguracaoSistema config = ConfiguracaoSistema.getInstancia();
        StringBuilder status = new StringBuilder();

        status.append(config.getTerritorio().verificarStatus()).append("\n\n");
        
        status.append("Limiares (ATENÇÃO / ALERTA / CRÍTICO): \n");
        for(Map.Entry<String, Limiar> entrada : config.getTerritorio().listarLimiares().entrySet()){
            Limiar limiar = entrada.getValue();
            status.append(String.format("  %-10s %.2f / %.2f / %.2f %s%n", entrada.getKey(),
                    limiar.getAtencao(), limiar.getAlerta(), limiar.getCritico(), unidadeDoTipo(entrada.getKey())));
        }

        status.append(String.format("%nPrevisões: média móvel com %d leituras | comparação de 2 períodos de %d leituras%n%n",
            config.getJanelaMediaMovel(), config.getTamanhoPeriodoComparacao()));
        
        status.append("Sensores:\n");
        for(Sensor sensor : sensores.values()){
            status.append(String.format(" [%d] %s | %d leituras no banco%n", sensor.getId(), sensor.verificarStatus(), medicaoDao.contarPorSensor(sensor.getId())));
        }
        
        List<Alerta> alertas = alertaDao.listarTodos();
        Map<NivelAlerta, Integer> porNivel = new LinkedHashMap<>();
        for(NivelAlerta nivel : NivelAlerta.values()){
            porNivel.put(nivel, 0);
        }
        for(Alerta alerta : alertas){
            porNivel.merge(alerta.getNivel(), 1, Integer :: sum);

        }
        status.append(String.format("%nAlertas emitidos: %d", alertas.size()));
        for(Map.Entry<NivelAlerta, Integer> entrada : porNivel.entrySet()){
            status.append(String.format(" | %s: %d", entrada.getKey().getDescricao(), entrada.getValue()));

        }
        status.append("\n");
        return status.toString();
        
    }
}