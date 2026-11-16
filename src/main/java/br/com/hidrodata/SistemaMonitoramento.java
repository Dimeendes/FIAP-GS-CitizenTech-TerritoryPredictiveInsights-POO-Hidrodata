package br.com.hidrodata;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import br.com.hidrodata.config.ConfiguracaoSistema;
import br.com.hidrodata.domain.model.Alerta;
import br.com.hidrodata.domain.model.DadosTerritorio;
import br.com.hidrodata.domain.model.Limiar;
import br.com.hidrodata.domain.model.Medicao;
import br.com.hidrodata.domain.model.Sensor;
import br.com.hidrodata.exception.DadosInvalidosException;
import br.com.hidrodata.exception.FonteIndisponivelException;
import br.com.hidrodata.service.ResultadoLeitura;
import br.com.hidrodata.service.ResultadoPrevisao;
import br.com.hidrodata.service.ServicoMonitoramento;

@Component 
public class SistemaMonitoramento implements CommandLineRunner{
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/mm/yyyy HH:mm:ss");
    private static final Map<Integer, String> STATUS_SENSOR = Map.of(1, "ATIVO", 2, "INATIVO", 3, "MANUTENCAO");

    private final ServicoMonitoramento servico;
    private Scanner scanner;

    public SistemaMonitoramento(ServicoMonitoramento servico){
        this.servico = servico;
    }

    @Override
    public void run(String... args){
        scanner = new Scanner(System.in);
        System.out.println("=== HIDRODATA - Sistema de monitoramento hídrico ===");
        
        boolean executando = true;
        while(executando){
            exibirMenu();
            try{
                int opcao = lerInteiro("Escolha uma opção: ");
                switch(opcao){
                    case 1 -> verificarSensores();
                    case 2 -> rodarPrevisoes();
                    case 3 -> verHistorico();
                    case 4 -> gerenciarDadosTerritorio();
                    case 5 -> System.out.println("\n" + servico.gerarStatusCompleto());
                    case 6 -> executando = false;
                    case 7 -> System.out.println("Opção inválida, digite um número de 0 a 5");
                }
            }catch (DadosInvalidosException e){
                System.out.println("Dado inválido: " + e.getMessage());
            }catch(FonteIndisponivelException e){
                System.out.println("Fonte de dados indisponível: " + e.getMessage());
            }   

        }
        System.out.println("Sistema encerrado");
    }
    private void exibirMenu(){
        System.out.println();
        System.out.println("1 - Verificar sensores (ler e gravar medições)");
        System.out.println("2 - Rodar previsões e emitir alertas");
        System.out.println("3 - Ver histórico");
        System.out.println("4 - Gerenciar dados do território");
        System.out.println("5 - Status completo");
        System.out.println("6 - Sair");
    }

    private void verificarSensores(){
        System.out.println("\n--- Leitura dos sensores ---");
        for(ResultadoLeitura resultado : servico.verificarSensores()){
            Sensor sensor = resultado.sensor();
            if(resultado.foiLido()){
                System.out.printf("[OK]  %s: %.2f %s (gravado com id %d)%n", sensor.getNome(), resultado.medicao().getValor(), servico.unidadeDoTipo(sensor.retornarTipo()), resultado.medicao().getId());
            }else{
                System.out.println("[FALHA] " + resultado.erro());
            }
        }
    }

    private void rodarPrevisoes(){
        System.out.println("\n---Previsões---");
        List<Alerta> alertasEmitidos = new ArrayList<>();
        for(ResultadoPrevisao resultado : servico.gerarPrevisoes()){
            System.out.printf("%n%s | %s%n", resultado.sensor().getNome(), resultado.estrategia());
            if(resultado.foiCalculada()){
                System.out.printf(" Valor previsto: %.2f %s%n", resultado.valorPrevisto(), servico.unidadeDoTipo(resultado.sensor().retornarTipo()));
                System.out.println(" " + resultado.explicacao());
                if(resultado.gerouAlerta()){
                    alertasEmitidos.add(resultado.alerta());
                }else{
                    System.out.println(" Dentro dos limites: nenhum alerta.");
                }
            }else{
                System.out.println(" Sem previsão: " + resultado.aviso());
            }
        }

        System.out.println("\n--- Alertas emitidos nessa rodada: " + alertasEmitidos.size() + " ---");
        for(Alerta alerta : alertasEmitidos){
            System.out.println(alerta.getMensagem());
        }
    }

    private void verHistorico(){
        System.out.println("\n1 - Medições de um sensor");
        System.out.println("2 - Alertas já emitidos");
        int opcao = lerInteiro("Escolha: ");
        switch(opcao){
            case 1 -> listarMedicoesDeSensor();
            case 2 -> listarAlertas();
            default -> System.out.println("Opção inválida");
        }
    }

    private void listarMedicoesDeSensor(){
        listarSensores();
        int sensorId = lerInteiro("Id do sensor: ");
        Sensor sensor = servico.buscarSensorPorId(sensorId);
        List<Medicao> historico = servico.buscarHistoricoSensor(sensorId);

        System.out.println("\n--- Histórico de " + sensor.getNome() + " (" + historico.size() + "medições) ---");
        if(historico.isEmpty()){
            System.out.println("Nenhuma medição gravada ainda. Use a opção 1 do menu principal.");
        }
        String unidade = servico.unidadeDoTipo(sensor.retornarTipo());
        for(Medicao medicao : historico){
            System.out.printf(" %s %10.2f %s%n", medicao.getDataHora().format(FORMATO_DATA), medicao.getValor(), unidade);
        }
    }

    private void listarAlertas(){
        List<Alerta>alertas = servico.listarAlertas();
        System.out.println("--- Alertas Emitidos (" + alertas.size() + ", mais recentes primeiro) ---");
        if(alertas.isEmpty()){
            System.out.println("Nenhum alerta emitido até agora.");
        }
        for(Alerta alerta : alertas){
            System.out.println(alerta.getDataHora().format(FORMATO_DATA) + " " + alerta.getMensagem());
        }
    }

    private void gerenciarDadosTerritorio(){
        ConfiguracaoSistema config = ConfiguracaoSistema.getInstancia();
        DadosTerritorio territorio = config.getTerritorio();

        System.out.println("\n" + territorio.verificarStatus());
        System.out.println("1 - Alterar população");
        System.out.println("2 - Alterar gasto médio (L/dia por pessoa)");
        System.out.println("3 - Alterar limiares de um tipo de sensor");
        System.out.println("4 - Alterar janela da média móvel (atual: " + config.getJanelaMediaMovel() + ")");
        System.out.println("5 - Alterar período da comparação (atual: " + config.getTamanhoPeriodoComparacao() + ")" );
        System.out.println("6 - Alterar status de um sensor");
        int opcao = lerInteiro("Escolha: ");

        switch (opcao){
            case 1 -> territorio.atualizarPopulacaoTerritorio(lerInteiro("Nova população: "));
            case 2 -> territorio.atualizarGastoMedioLitros(lerDecimal("Novo gasto médio: ")); 
            case 3 -> alterarLimiar(territorio);
            case 4 -> config.alterarJanelaMediaMovel(lerInteiro("Nova janela (2 a 50 leituras): "));
            case 5 -> config.alterarTamanhoPeriodoComparacao(lerInteiro("Novo período (1 a 25 leituras): "));
            case 6 -> alterarStatusSensor();
            default ->{
                 System.out.println("Opção inválida.");
                return;
            }
        }
        System.out.println("Alteração salva.");

    }

    private void alterarLimiar(DadosTerritorio territorio){
        System.out.println("Limiares atuais (ATENCAO / ALERTA / CRÍTICO): ");
        for(Map.Entry<String, Limiar> entrada : territorio.listarLimiares().entrySet()){
            Limiar limiar = entrada.getValue();
            System.out.printf(" %-10s %.2f / %.2f / %.2f %s%n", entrada.getKey(), limiar.getAtencao(), limiar.getAlerta(), limiar.getCritico(), servico.unidadeDoTipo(entrada.getKey()));

        }
        String tipo = lerTexto("Tipo de sensor: ").toUpperCase();
        territorio.buscarLimiar(tipo);
        double atencao = lerDecimal("Novo limite de ATENÇÃO: ");
        double alerta = lerDecimal("Novo limite de ALERTA: ");
        double critico = lerDecimal("Novo limite de CRÍTICO: ");
        territorio.alterarLimiar(tipo, atencao, alerta, critico);
    }

    private void alterarStatusSensor(){
        listarSensores();
        int sensorId = lerInteiro("Id do sensor: ");
        System.out.println("1 - ATIVO | 2 - INATIVO | 3 - MANUTENCAO");
        int opcao = lerInteiro("Novo status: ");
        String novoStatus = STATUS_SENSOR.get(opcao);
        if(novoStatus == null){
            throw new DadosInvalidosException("Status " + opcao + " não existe. Escolha 1,2 ou 3.");
        }
        servico.alterarStatusSensor(sensorId, novoStatus);
    }

    private void listarSensores(){
        System.out.println("Sensores");
        for(Sensor sensor : servico.listarSensores()){
            System.out.println(" [" + sensor.getId() + "]" + sensor.verificarStatus());

        }
    }

    private String lerTexto(String mensagem){
        System.out.println(mensagem);
        return scanner.nextLine().trim();
    }

    private int lerInteiro(String mensagem){
        String texto = lerTexto(mensagem);
        try{
            return Integer.parseInt(texto);
        } catch(NumberFormatException e){
            throw new DadosInvalidosException("'" + texto + "' não é um número inteiro");
        }
    }

    private double lerDecimal(String mensagem){
        String texto = lerTexto(mensagem).replace("," , ".");
        try{
            return Double.parseDouble(texto);
        } catch(NumberFormatException e){
            throw new DadosInvalidosException("'" + texto + "' não é um número válido");
        }
    }

}
