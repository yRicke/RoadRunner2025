# FlywheelAdvanced — ajuste de kS, kV e kP

As classes partiram do [vídeo de Brogan M. Pratt](https://www.youtube.com/watch?v=KVPdbWcAyUY)
e foram adaptadas ao projeto: dois motores acoplados ao mesmo eixo, leitura de
RPM pelo primeiro motor e alvos de **2.200 e 2.800 RPM** nos tuners.

O controle usado é feedforward (`kS + kV × RPM alvo`) somado à correção
proporcional (`kP × erro`). Ajuste na ordem **kS → kV → kP**.

## Antes de começar

- Configure `launcher_motor_one` e `launcher_motor_two` no robô. O encoder
  usado para medir o eixo é o de `launcher_motor_one`.
- Calibre com os **dois motores acoplados e funcionando juntos**, como na partida.
  Nesta montagem, de frente um para o outro, o primeiro está em `REVERSE` e o
  segundo em `FORWARD`; ambos recebem a mesma potência e usam `RUN_WITHOUT_ENCODER`.
- Em `Flywheel`, confira `encoderCPM` e `gearRatio`, atualmente 28 e 1.
  A conversão é `RPM = (ticks/s / encoderCPM × 60) / gearRatio`. Com potência
  positiva no sentido de lançamento, a leitura usada pelo controlador deve ser positiva.
- Os ganhos atuais são valores salvos de uma calibração anterior. Adicionar um
  motor muda a resposta do conjunto: confira os três ganhos novamente.
- Use uma bateria com carga representativa da partida. O código comanda uma
  fração de potência, sem compensação explícita da tensão da bateria.
- Selecione `kS Tuner`, `kV Tuner` ou `kP Tuner` no grupo `Flywheel` do Driver
  Station e use INIT/START. As classes de ajuste ficam em `Tunning/`, e o
  `Flywheel Test` fica em `Tests/`.
  Os tuners aplicam potência continuamente depois do START. Em `kV Tuner` e
  `kP Tuner`, o alvo inicial já é **2.800 RPM**.

## Uso de bolinhas e troca de RPM

| Etapa | Usar bolinhas? | Alternar A/B? | O que observar |
| --- | --- | --- | --- |
| `kS` | Não | Não há alvo de RPM | Menor potência que inicia o movimento |
| `kV` | Não | Sim, esperando estabilizar em cada alvo | RPM final próximo de 2.200 e 2.800 |
| `kP`, ajuste inicial | Não | Sim | Rapidez da resposta e oscilações após a troca |
| Validação dos lançamentos | Sim, pelo alimentador | Manter o alvo de lançamento | Queda e recuperação de RPM após cada bola |

A troca de RPM pelo controle provoca uma mudança no alvo e ajuda a avaliar a
resposta. Uma bolinha provoca uma carga no eixo e reduz sua velocidade. Faça os
dois testes: responder bem à troca A/B não garante boa recuperação em lançamentos.
O ensaio com bolas complementa o ajuste por troca de alvos usado no guia original.

## Controles

| Controle | Função |
| --- | --- |
| Direcional ↑ / ↓ | Aumentar / diminuir o ganho do tuner atual |
| Direcional → / ← | Aumentar / diminuir o passo do ajuste |
| A, em kVTuner/kPTuner | Alvo de 2.800 RPM |
| B, em kVTuner/kPTuner | Alvo de 2.200 RPM |

Leia `Step`, o ganho e `RPM` na telemetria. Em kV/kP, `Target RPM` mostra o alvo
selecionado; em kP, `Error` é `alvo − medido`.

Os passos disponíveis são `0.000001`, `0.00001`, `0.0001`, `0.001` e `0.01`.
O índice inicial é 4: **o passo inicial é `0.01`**.
Antes de alterar kV/kP, use o direcional esquerdo para diminuir
o passo e confira `Step`. Por exemplo, com alvo de 2.800 RPM, aumentar kV em
`0.000001` aumenta a potência calculada em `0.0028`; um passo de `0.01` seria enorme.

Os botões alteram o ganho em memória. **Eles não salvam no código nem transferem
valores entre classes.** Anote os valores ao terminar cada etapa.

## 1. Ajustar kS

[43:57 — How to Tune kS](https://www.youtube.com/watch?v=KVPdbWcAyUY&t=2637s)

O objetivo é estimar a potência para vencer o atrito, sem tentar atingir
2.200 ou 2.800 RPM. O `kS Tuner` aplica diretamente `setMotorPower(kS)` aos dois motores.

1. Deixe o shooter sem bolinhas. Para refazer a calibração desde zero, coloque
   `kS = 0` em `Tunning/kSTuner.java` antes de enviar o programa ao robô; o
   campo atual ainda contém o valor anterior.
2. Execute `kS Tuner`. Aumente kS devagar com ↑ até a flywheel começar a girar.
3. Perto desse ponto, reduza `Step` com ← e refine o valor. Não procure uma
   oscilação de RPM nesta etapa: procure o limite entre parado e girando.
4. Diminua o ganho até parar e repita a partida. Confira o resultado várias
   vezes, pois manter o eixo girando pode exigir menos potência que iniciá-lo.
5. Anote o kS e copie-o para `Tunning/kVTuner.java` antes da próxima etapa.

## 2. Ajustar kV

[51:51 — How to Tune kV](https://www.youtube.com/watch?v=KVPdbWcAyUY&t=3111s)

O objetivo é aproximar a velocidade desejada usando somente
`potência = kS + kV × RPM alvo`. O `kV Tuner` não usa kP.

1. Ainda sem bolinhas, confira se o kS encontrado está em `Tunning/kVTuner.java`
   e execute `kV Tuner`. O kV anterior pode servir de ponto de partida.
2. Escolha um passo pequeno antes de alterar o ganho. Pressione A para
   **2.800 RPM** e espere a velocidade estabilizar.
3. Se o RPM estabilizado ficar abaixo do alvo, aumente kV; se ficar acima,
   diminua. Após cada alteração, espere novamente antes de julgar o resultado.
4. Pressione B para **2.200 RPM**, espere estabilizar e confira o mesmo ganho.
   Repita A/B com tempo suficiente em cada alvo; apertar rapidamente os botões
   não permite avaliar a velocidade de equilíbrio.
5. Se um ganho funciona num alvo e erra muito no outro, confira kS, a conversão
   de RPM e a montagem. Um único modelo linear pode não coincidir exatamente
   com toda a faixa; não use kP para esconder um feedforward claramente incorreto.
6. Anote kS e kV e copie ambos para `Tunning/kPTuner.java`.

Não use bolas para decidir kV: a queda durante o lançamento é transitória.
Nesta etapa, julgue o RPM estabilizado sem alimentação.

## 3. Ajustar kP

[58:34 — How to Tune kP](https://www.youtube.com/watch?v=KVPdbWcAyUY&t=3514s)

O objetivo é corrigir o erro mais rapidamente. A potência passa a ser
`kS + kV × RPM alvo + kP × (RPM alvo − RPM medido)`.

### Primeiro: trocar o alvo sem bolinhas

1. Mantenha kS/kV já ajustados. Para refazer o ajuste, coloque `kP = 0` em
   `Tunning/kPTuner.java` antes de enviar o programa; o campo atual guarda o
   valor anterior e não começa automaticamente em zero.
2. Execute `kP Tuner` sem bolinhas. Escolha um passo pequeno e aumente kP aos poucos.
3. Alterne **B (2.200) → A (2.800)** e espere estabilizar. Depois faça
   **A (2.800) → B (2.200)**. Observe `RPM`, `Target RPM` e `Error` nas duas direções.
4. Procure uma aproximação rápida do alvo. Se continuar lento, experimente
   aumentar kP em passos pequenos. Se passar do alvo e ficar indo e voltando
   repetidamente, reduza kP e refine o passo.
5. Não tente eliminar toda variação de um ou dois valores do encoder: procure
   oscilações repetidas e relevantes para o lançamento, não cada atualização da tela.

### Depois: validar com bolinhas a 2.800 RPM

Os tuners controlam somente a flywheel: **A/B não acionam o Trigger nem o intake**.
Para testar a recuperação com o alimentador do robô:

1. Anote os ganhos e copie kS, kV e kP para `Flywheel.java`. Envie o programa atualizado.
2. Execute `TeleOpMainBrabo`. Ele mantém a flywheel em **2.800 RPM**; o bumper
   direito do gamepad 1 comanda o Trigger. Use a alimentação normal do mecanismo.
3. Espere o RPM estabilizar antes da primeira bola. Lance uma bola e observe
   a queda de RPM e a volta ao alvo, sem mudar o alvo durante a medição.
4. Repita com bolas individuais e depois na cadência de disparo da partida.
   Observe se a velocidade já se recuperou antes de cada bola seguinte.
5. Se a recuperação estiver lenta, retorne ao ajuste de kP e aumente aos poucos.
   Se houver oscilação persistente ou ultrapassagem excessiva do alvo, reduza.
   Confira também alimentação, atrito e capacidade do conjunto se o erro persistir.
6. Anote os valores finais. Um bom resultado deve responder bem às trocas A/B
   e aos lançamentos no RPM de uso.

A queda logo após a bola é esperada. Ela não é, por si só, uma oscilação do
controlador. Oscilação é continuar alternando acima e abaixo do alvo, sem novas
bolas nem mudanças de RPM. Os passos com bolinhas complementam o método por
troca de alvos e seguem a orientação de recuperação sob carga da
[REV para FTC](https://docs.revrobotics.com/ftc-kickoff-concepts/decode-2025-26/programming-tips-and-tricks).

## Onde salvar os ganhos

| Resultado | Copiar para |
| --- | --- |
| kS encontrado em `kSTuner` | Campo `kS` de `kVTuner` |
| kS e kV validados em `kVTuner` | Campos `kS` e `kV` de `kPTuner` |
| kS, kV e kP finais | Campos privados `kS`, `kV` e `kP` de `Flywheel` |

Anote antes de sair do tuner. Alterar os campos de uma classe não atualiza as
outras; um novo envio ou reinício do aplicativo pode descartar ajustes em memória.
Reavalie a calibração ao mudar motores, transmissão, flywheel ou atrito da montagem.

O procedimento de ajustar o feedforward antes da correção proporcional também
é descrito no [guia de flywheel da WPILib](https://docs.wpilib.org/en/stable/docs/software/advanced-controls/introduction/tuning-flywheel.html).

## Usar a Flywheel final

[1:03:25 — Programming the Final Custom Motor Controller](https://www.youtube.com/watch?v=KVPdbWcAyUY&t=3805s)

A API do subsistema foi adaptada ao padrão do projeto. Inicialize uma vez com
`new Flywheel(hardwareMap, telemetry)`, dentro de `init()` do OpMode iterativo ou
no início de `runOpMode()` do LinearOpMode, antes de `waitForStart()`.

No loop do TeleOp ou Auto, chame `flywheel.run(rpmDesejado)` a cada iteração.
Para desligar, use `flywheel.stopMotors()` ou `flywheel.run(0)`. RPM negativo ou
não finito também desliga o motor.

Chame `flywheel.sendTelemetry()` no loop para adicionar RPM alvo, RPM medido,
erro de RPM e potência à telemetria. O OpMode continua responsável por chamar
`telemetry.update()` depois de reunir os dados dos subsistemas.
