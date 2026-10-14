# Launcher com controle de RPM

`LauncherPIDado` controla o motor `launcher_motor` usando o encoder e o PIDF de
velocidade do hub em `RUN_USING_ENCODER`. O método `run(rpm)` recebe RPM e converte
para ticks por segundo: `rpm * TICKS_PER_REV / 60`. A velocidade medida faz a
conversão inversa. Essa é a unidade esperada por
[`DcMotorEx.setVelocity()` no SDK FTC](https://javadoc.io/static/org.firstinspires.ftc/RobotCore/11.0.0/com/qualcomm/robotcore/hardware/DcMotorEx.html).

O hub corrige a velocidade pelo encoder continuamente. Cada chamada de `run`
atualiza o alvo e verifica se os ganhos foram alterados no Dashboard. A precisão e
a recuperação após um disparo dependem da calibração e da capacidade do motor.

## Uso em um TeleOp

Crie o subsistema uma vez, antes do START:

```java
LauncherPIDado launcher = new LauncherPIDado(hardwareMap, telemetry);
```

Dentro do loop, passe o RPM desejado a cada iteração:

```java
launcher.run(rpmDesejado);
launcher.sendTelemetry();
telemetry.update();
```

Alterar `rpmDesejado` atualiza o alvo tanto ao aumentar quanto ao diminuir o RPM.
`run(0)` e `stop()` desligam o motor. RPM negativo, NaN ou infinito também desliga.
Chamadas a `stop()` no `finally` do OpMode garantem o desligamento ao encerrar.
O exemplo completo dessa estrutura está em `LauncherPIDTest`.

## Teste e calibração no FTC Dashboard

1. Configure o motor e seu encoder no robô com o nome `launcher_motor`.
2. Selecione o OpMode `LauncherPIDTest` e pressione INIT.
   Atualize o painel de configuração do Dashboard para visualizar `RPM = 0` e
   `ENABLED = false`, reiniciados a cada INIT.
3. Em **LauncherPIDado**, ajuste `TICKS_PER_REV` e `MAX_RPM` conforme o motor.
   Os valores iniciais são 28 ticks por volta e 6.000 RPM, seguindo o subsistema
   `LauncherVelocity`. Ambos se referem ao eixo correspondente ao encoder; uma
   transmissão externa exige converter o RPM da flywheel para o RPM desse eixo.
4. Pressione START. Em **LauncherPIDTest**, defina `RPM` e `ENABLED = true`.
   O teste começa com `RPM = 0` e `ENABLED = false`. Salve as alterações no Dashboard.
5. Trace `Launcher RPM alvo`, `Launcher RPM medido` e `Launcher erro RPM` no gráfico.
6. Ajuste `P`, `I`, `D` e `F` em **LauncherPIDado** e salve. Os novos ganhos são
   aplicados no próximo loop com o motor habilitado e RPM positivo.
7. Experimente vários alvos, aumente e reduza o RPM, e observe a recuperação de
   velocidade depois de um disparo. Para desligar, use `ENABLED = false`, `RPM = 0`
   ou STOP.

Os ganhos iniciais (`P = 10`, `I = 0`, `D = 0`, `F = 11.7`) são um ponto de partida.
Eles pertencem ao PIDF do hub, que trabalha em ticks/s. Ajuste `F` para aproximar a
velocidade do alvo e `P` para corrigir desvios sem oscilação. Adicione `I` aos poucos
se restar erro constante; `D` pode ajudar no amortecimento, mas amplifica ruído do
encoder. Ao alterar os ticks por volta, confira novamente os ganhos.

O alvo é limitado a `MAX_RPM`. Configurações não finitas, ticks por volta ou RPM
máximo menores ou iguais a zero, ou ganhos negativos desligam o motor e mostram
`Configuração inválida`. Ajustes feitos pelo Dashboard valem em execução; copie os
valores calibrados para os campos da classe para manter os padrões após reiniciar
o aplicativo. A exposição dos campos segue a
[documentação de configuração do FTC Dashboard](https://acmerobotics.github.io/ftc-dashboard/features.html).

## Verificação automatizada

```powershell
.\gradlew.bat :TeamCode:testDebugUnitTest :TeamCode:assembleDebug
```

Os testes usam um motor simulado para verificar conversões, mudanças de alvo,
limites, atualização dos ganhos, telemetria e desligamento. A calibração e a
estabilidade mecânica precisam ser verificadas no robô com `LauncherPIDTest`.
