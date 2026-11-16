# Hidrodata

Sistema de monitoramento de sensores e geração de previsões e alertas para apoiar o acompanhamento de dados hídricos.

## Integrantes do grupo

| Nome | RM |
| -- | -- |
| Thiago Sobral de Alvarenga | 562695 |
| Diego Antonio Silva Mendes | 565509 |

## Como rodar o projeto

**Pré requisitos**
- **JDK 25**(Mesma versão definida em 'java.version' no arquivo pom.xml)
- **Maven 3.9** ou superior

Não é necessário criar nem instalar nenhum banco de dados, o projeto foi desenvolvido usando H2 gravado em arquivo. Durante a primeira execução, o arquivo `data/hidrodata.mv.db` é criado na raiz do porjeto e as tabelas são montadas pelo src/main/resources/schema.sql. Como o banco de dados desse projeto é em arquivo, ele persiste mesmo depois de fechar/reiniciar o programa.

**Executar o sistema**
Na raiz do projeto use o comando:

mvn spring-boot:run

O menu irá aparecer no console, então é só escolher uma das opções para utilizar o programa.

**Roteiro rápido para testes**
1. Use a opção 1 do menu pelo menos 6 vezes. Cada rodada lê os sensores e grava uma medição por sensor. O sensor 4 começa em manutenção e mostra a falha propositalmente(é possível alterar isso diretamente pelo programa).
2. Use a opção 2. Com 5 leituras mínimas, a média móvel já calcula, com 6, a comparação de períodos também. Se alguma previsão passar dos limites, o alerta aparece com o sensor, o valor previsto, o limite ultrapassado e uma ação sugerida.
3. Use a opção 3 para ver o histórico de um sensor e os alertas emitidos.
4. Feche com 6, rode de novo e use a opção 3 para verificar que o histórico se manteve.

**Executar testes**

Na raiz do projeto use o comando:

mvn test

Dessa forma no console serão exibidos a quantidade de testes realizados, e retornando 0 falhas.

## Justificativa dos padrões criacionais

Neste projeto foram utilizados dois padrões, codependentes entre si: a fabríca lê os parâmetros guardados no Singleton na hora de criar cada estratégia.

**Singleton: ConfiguracaoSistema.java**
Aplicação: o construtor é private e a única instância é criada uma vez, no campo "private static final INSTANCIA". Todo o sistema acessa a configuração por "COnfiguracaoSistema.getInstancia()". Ela guarda o território "DadosTerritorio", os limiares de ATENÇÃO, ALERTA E CRÍTICO de cada senor, a janela de média móvel e o período de comparação.

Por que ali: os limiares precisam ter uma única fonte. se o menu, o serviço de alertas e a fábrica tivessem cada um a sua cópia, alterar um limiar pela opção 4 do menu ("4 - Gerenciar dados do território") não mudaria os alertas emitidos pelo serviço. Com uma só instância só, a alteração vale para todo o sistema. A instância é criada no carregamento da calsse, o que é mais simples do que criar e modificar várias a todo momento, além de também ser mais seguro, mesmo com várias threads.

Por que não em outro lugar: sensores, medições e alertas existem em várias cópias, uma para cada sensor ou leitura. Transformá-los em Singleton seria um erro de modelagem.

**Factory: FabricaPrevisao.java**

Aplicação: a fábrica guarda uma Map(Uma estrutura de dados que armazena elementos no formato de pares de chave e valor, sendo a chave única, o que possibilita sobreescrever valores antigos)que liga cada código MEDIA_MOVEL, COMPARACAO_PERIODOS a uma função que cria a estratégia correspondente. Escolher a estratégia é uma consulta ao mapa "criarEstrategia", sem if tipo == nem instanceof. O único if presente, serve para conferir se o código existe, se não existir, a fábria lança a excessão DadosInvalidosException com a lista de opções válidas. Por fim, o serviço chama criarTodasEstrategias() e trata cada resultado como EstrategiaPrevisao, sem saber qual classe concreta recebeu.

Por que ali: criar uma estratégia depende de um código e de parâmetros de sua configuração. A janela da média móvel e o período de comparação são lidos do Singleton no momento da criação, então uma mudança feita pelo menu já vale na próxima previsão (como explicado na sessão "Singleton" acima). A criação fica concentrada num lugar só: uma estratégia nova exige uma linha a mais na fábrica, e nenjum outro arquivo muda.

Por que não em outro lugar: "Medicao" e "Alerta" tem uma forma só e já validam os próprios dados no construtor. Uma fábrica para eles só aumentaria o código sem necessidade e sem tirar nenhuam decisão do resto do sistema.


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

## O uso da inteligência Artificial

Para esse projeto, utilizamos as IAs em diferêntes momentos, sendo seu principal objetivo ser um assistente que ajudasse a entender onde erros ocorreram, sugestões de melhoria e ferramenta de estudo.

As IAs foram usadas para verificar se alguma parte do código estava errada (sejam possíveis bugs ou problemas de clean code). Alguns desses problemas são encontrados juntos a outros commits feitos, já outros, foram corrijidos antes mesmo da versão vir ao repositório. 

Ao fim das etapas, a IA também era utilizada para como forma de saber se algum trecho poderia ser feito de maneira mais eficiente ou correta, seja eliminando código extra (if sendo substituidos por switch cases) ou mudanças de lógica.

Pelo curto prazo e por existirem diversas outras atividades com matérias completamente diferentes dessa atividade, a IA também foi utilizada para pesquisa de conceitos ainda não dominados.

Apesar disso tudo, cada sugestão ou correção feitas por elas, foram conferidas e adaptados pelos integrantes, testando se o projeto ainda mantinha sua estrutura e funcionamento original