package br.com.hidrodata.domain.model;

import java.util.Random;

import br.com.hidrodata.exception.FonteIndisponivelException;

public class SensorVazamento extends ComponenteTerritorial implements Sensor {
    private static final double LIMITE_DETECCAO_LITROS_HORA = 50;
    private boolean vazamentoDetectado;
    private Random random = new Random();
    

    public SensorVazamento(int id, String nome, String regiao, String status) {
        super(id, nome, regiao, status);
    }

    @Override
    public double lerValor() {
        if(!verificarFuncionamento()) {
            throw new FonteIndisponivelException("O sensor de vazamento '" +getNome() + "'(id " + getId() + ") está " + getStatus() + " e não está funcionando.");
        }
        if(random.nextDouble() < 0.05) {
            throw new FonteIndisponivelException("O sensor de vazamento '" + getNome() + "'(id " + getId() + ") está com falha temporária e não conseguiu ler o valor.");
        }
        double perda = random.nextDouble() * 300;
        vazamentoDetectado = perda > LIMITE_DETECCAO_LITROS_HORA;
        return perda;
    }

    @Override
    public boolean verificarFuncionamento() {
        return "ATIVO".equalsIgnoreCase(getStatus());
    }

    @Override
    public String retornarTipo() {
        return "Vazamento";
    }

    @Override
    public String verificarStatus() {
        return "Sensor de Vazamento '" + getNome() + "' (id " + getId() + "): " + getStatus() + (vazamentoDetectado ? " - Vazamento detectado!" : " - Sem vazamento detectado." );
    }

    public boolean isVazamentoDetectado() {
        return vazamentoDetectado;
    }
}
