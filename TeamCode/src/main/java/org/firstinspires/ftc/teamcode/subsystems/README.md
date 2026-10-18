# Organização dos subsistemas

Cada pasta reúne as classes do subsistema, seus OpModes de teste em `Tests/`
e seus OpModes de ajuste em `Tunning/`, quando existentes.
Os grupos no Driver Station seguem o nome do subsistema.

| Pasta | Classes | Grupo no Driver Station |
| --- | --- | --- |
| `AprilTagWebcam/` | `AprilTagWebcam` | `AprilTagWebcam` |
| `Drive/` | `Drive`, `DriveField` | `Drive` |
| `FlywheelAdvanced/` | `Flywheel` | `Flywheel` |
| `Intake/` | `Intake` | `Intake` |
| `Launcher/` | `LauncherPower`, `LauncherVelocity`, `LauncherPIDado` | `Launcher` |
| `Trigger/` | `Trigger` | `Trigger` |
| `Voltage/` | `Voltage` | `Voltage` |

Os testes e ajustes nativos do Road Runner ficam no pacote `teamcode.tuning`,
fora de `subsystems/`, e aparecem no grupo `quickstart` do Driver Station.
A pasta `Drive/Tests/` reúne os testes próprios `DriveTest` e `DriveFieldTest`.

Os testes automatizados JUnit continuam em `TeamCode/src/test/java`, seguindo
a mesma hierarquia de subsistemas. Eles são executados no computador, enquanto
os OpModes de `Tests/` e `Tunning/` são executados no robô.
