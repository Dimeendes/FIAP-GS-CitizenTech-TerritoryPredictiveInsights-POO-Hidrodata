# Hidrodata

Sistema de monitoramento de sensores e geração de previsões e alertas para apoiar o acompanhamento de dados hídricos.

## Integrantes do grupo

| Nome | RM |
| -- | -- |
| Thiago Sobral de Alvarenga | 562695 |
| Diego Antonio Silva Mendes | 565509 |

## Público beneficiado e decisões apoiadas

O público direto são as equipes de operação e manutenção de redes de abastecimento e os gestores públicos responsáveis pelo acompanhamento dos recursos hídricos. Indiretamente, a solução busca beneficiar moradores que podem ser afetados por vazamentos, consumo fora do padrão ou problemas de pressão.

Com o histórico de medições e as previsões de consumo, vazamento e pressão, o sistema apoia a decisão de quais sensores ou regiões devem ser verificados primeiro e se é necessário priorizar uma inspeção ou manutenção. O alerta é um indício para orientar a análise da equipe, não uma confirmação de ocorrência nem uma ordem automática de intervenção.

## Decisões da equipe

- Desenvolver um protótipo de terminal em Java com Spring Boot, com menu para leitura dos sensores, consulta de histórico, previsões, alertas e configuração do território.
- Persistir medições e alertas em banco H2 usando JDBC, permitindo consultar registros entre operações do sistema.
- Modelar sensores de consumo, pressão e vazamento e oferecer duas estratégias simples de previsão: média móvel e comparação entre períodos.
- Permitir ajustar os limiares de alerta e as janelas de previsão para adequá-los ao cenário configurado.
- Não apresentar previsão quando o histórico não atende ao mínimo exigido pela estratégia e explicar o resultado para apoiar a avaliação humana.
- Usar leituras simuladas no protótipo; elas permitem exercitar o fluxo do sistema, mas não representam dados de uma rede real.

## Reflexão crítica e uso responsável

O Hidrodata organiza medições e destaca tendências que poderiam passar despercebidas, ajudando a equipe a direcionar verificações. Porém, este protótipo usa sensores simulados e previsões baseadas em janelas curtas; não foi validado com dados operacionais reais nem considera, por si só, fatores como clima, obras ou manutenção. Assim, não deve ser usado isoladamente para confirmar problemas ou decidir intervenções. Uma evolução importante é integrar dados reais e avaliar a qualidade das previsões com profissionais e comunidades do território, revisando cobertura dos sensores e limiares para reduzir alertas inadequados e situações não detectadas.

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

**Foi feito uso de IA para procura de erros de clean code**