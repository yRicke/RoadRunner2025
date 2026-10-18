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

Os testes de localização e spline do Road Runner ficam em `Drive/Tests/`.
Seu registrador e o tuner de feedback ficam em `Drive/Tunning/`; os OpModes
registrados por `TuningOpModes` aparecem no grupo `Drive`.

Os testes automatizados JUnit continuam em `TeamCode/src/test/java`, seguindo
a mesma hierarquia de subsistemas. Eles são executados no computador, enquanto
os OpModes de `Tests/` e `Tunning/` são executados no robô.
