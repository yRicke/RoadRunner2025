# FlywheelAdvanced — ajuste de kS, kV e kP

Classes transcritas do [vídeo de Brogan M. Pratt](https://www.youtube.com/watch?v=KVPdbWcAyUY),
com o pacote deste repositório e apenas o primeiro motor (`m1`, nome `leftMotor`).
O controlador final de 1:03:25 já está na classe `Flywheel`.

## Antes de começar

- Configure o nome do motor com encoder;
- Em `Flywheel`, confira `encoderCPM` (28 no exemplo) e `gearRatio` (3 no exemplo).
  A conversão copiada é `RPM = (ticks/s / encoderCPM × 60) / gearRatio`.
  Substitua-os pelos valores encontrados no seu mecanismo durante o ajuste.
- Selecione `kSTuner`, `kVTuner` ou `kPTuner` no Driver Station e use INIT/START.
  Os tuners aplicam a potência continuamente depois do START, como no vídeo.

## Controles

| Controle | Função |
| --- | --- |
| Direcional ↑ / ↓ | Aumentar / diminuir o ganho do tuner atual |
| Direcional → / ← | Aumentar / diminuir o passo do ajuste |
| A, em kVTuner/kPTuner | Alvo de 1.500 RPM |
| B, em kVTuner/kPTuner | Alvo de 800 RPM |

Leia `Step`, o ganho e `RPM` na telemetria. Os passos disponíveis são `0.000001`,
`0.00001`, `0.0001`, `0.001` e `0.01`. O índice inicial copiado é 4, portanto o
passo inicial é `0.01`, apesar do comentário `Start at 0.001` no código do vídeo.

## 1. Ajustar kS

[43:57 — How to Tune kS](https://www.youtube.com/watch?v=KVPdbWcAyUY&t=2637s)

1. Execute `kSTuner`. Ele aplica diretamente `setMotorPower(kS)`.
2. Aumente kS até a flywheel vencer o atrito e começar a girar.
3. Reduza o passo perto desse ponto para refinar o valor.
4. Confira a partida a partir da roda parada: manter o movimento pode exigir
   menos potência do que iniciar o movimento.
5. Anote o valor e copie-o para o campo `kS` de `kVTuner`.

## 2. Ajustar kV

[51:51 — How to Tune kV](https://www.youtube.com/watch?v=KVPdbWcAyUY&t=3111s)

1. Com o kS encontrado na etapa anterior, execute `kVTuner`.
2. A potência aplicada é `(kV × goalRPM) + kS`, sem correção proporcional.
3. Espere o RPM estabilizar. Aumente kV se o RPM estiver abaixo do alvo;
   diminua se estiver acima. Reduza o passo para os ajustes finos.
4. Alterne A/B para comparar a resposta em 1.500 e 800 RPM. O autor observa
   que um único kV pode não aproximar perfeitamente toda a faixa de velocidades.
5. Anote o valor e copie kS e kV para os respectivos campos de `kPTuner`.

## 3. Ajustar kP

[58:34 — How to Tune kP](https://www.youtube.com/watch?v=KVPdbWcAyUY&t=3514s)

1. Mantenha kS e kV já ajustados e execute `kPTuner`, começando com kP zero.
2. O tuner soma o feedforward à correção `kP × (goalRPM − RPM medido)`.
3. Aumente kP aos poucos e alterne A/B para observar a resposta e o `Error`.
4. Procure atingir o alvo rapidamente e recuperar velocidade após uma queda.
   Se houver oscilação ou excesso de correção, diminua kP e refine o passo.
5. Copie os três ganhos encontrados para os campos privados de `Flywheel`.
   A transferência entre as classes é manual, como no vídeo.

## Usar a Flywheel final

[1:03:25 — Programming the Final Custom Motor Controller](https://www.youtube.com/watch?v=KVPdbWcAyUY&t=3805s)

Inicialize uma vez com `flywheel.init(hardwareMap)`. No loop do TeleOp ou Auto,
chame `flywheel.setMotorRPM(rpmDesejado)` a cada iteração. Para desligar, use
`flywheel.stopMotors()`, que é o método de parada mostrado pelo autor.
