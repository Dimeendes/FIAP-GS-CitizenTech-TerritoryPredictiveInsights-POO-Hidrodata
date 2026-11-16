# Hidrodata

Sistema de monitoramento de sensores e geração de previsões e alertas para apoiar o acompanhamento de dados hídricos.

## Integrantes do grupo

| Nome | RM |
| -- | -- |
| Thiago Sobral de Alvarenga | 562695 |
| Diego Antonio Silva Mendes | 565509 |

## Limites e uso responsável

### Quando o sistema deve se calar?

O sistema não calcula uma previsão quando não há medições suficientes para a estratégia escolhida. Na configuração padrão, a média móvel precisa das **5 leituras mais recentes**. Com menos de 5, o resultado deve ser tratado como indisponível — não como zero nem como uma estimativa confiável. Esse número é um mínimo operacional, não uma garantia estatística de precisão.

A comparação de períodos precisa de duas janelas completas. Com o tamanho padrão de 3 leituras por período, são necessárias **6 medições**. Sem esse histórico, o sistema informa que não foi possível calcular a previsão.

Mesmo quando o mínimo é atendido, a previsão pode não ser confiável se os dados estiverem desatualizados, incompletos ou fora de contexto. O sistema calcula com as medições disponíveis; a quantidade mínima, por si só, não valida a qualidade nem a representatividade dos dados.

### O que os dados não representam?

Uma leitura representa o valor registrado por um sensor em determinado momento, não toda a realidade de uma região. Um sensor por região pode não revelar diferenças entre ruas, imóveis, trechos da rede ou horários. Os sensores cadastrados no sistema são referências para o monitoramento e não devem ser interpretados como cobertura completa do território.

As previsões extrapolam padrões recentes: a média móvel resume as últimas leituras, e a comparação de períodos projeta a variação entre duas janelas. Elas não explicam por que o valor mudou e não incorporam, por si só, fatores externos como manutenção, clima, obras ou falhas do sensor.

### Por que o alerta apoia a decisão da pessoa?

O alerta compara a previsão com limites configurados para cada tipo de medição e apresenta uma indicação de atenção. Ele não confirma sozinho que há um problema, não identifica sua causa e não executa uma ação corretiva. Cabe à pessoa responsável considerar o contexto, verificar a qualidade das medições e, quando necessário, confirmar a situação antes de decidir o que fazer.

Os limites são parâmetros operacionais que podem precisar de revisão conforme o contexto local. Portanto, tanto previsões quanto alertas devem ser usados como apoio à análise humana, não como decisão automática ou substituto de inspeção técnica.