package org.firstinspires.ftc.teamcode.subsystems;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class LauncherPower {

    private final DcMotorEx launcherMotorOne;
    private final DcMotorEx launcherMotorTwo;
    private final DcMotorEx indexMotor;

    private final ElapsedTime indexTimer = new ElapsedTime();

    private final Telemetry telemetry;

    double[] launcherPowers = {0.6, 0.65, 0.70, 0.75, 0.80};
    double[] indexPowers = {0.4, 0.45, 0.5, 0.55, 0.6, 0.65, 0.7, 0.75, 0.8};
    final int normalLauncherSelect = 2;
    final int normalIndexSelect = 4;
    int launcherPowerSelected = normalLauncherSelect;
    int indexPowerSelected = normalIndexSelect;
    long cooldownLauncher = 200;// 200ms
    long cooldownIndex = 200;// 200ms

    private double launcherPower = launcherPowers[launcherPowerSelected];
    private double indexPower = indexPowers[indexPowerSelected];
    private final ElapsedTime launcherTimer = new ElapsedTime();
    private final ElapsedTime indexChangeTimer = new ElapsedTime();

    // Construtor recebe o hardwareMap e telemetry do OpMode
    public LauncherPower(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;

        launcherMotorOne = hardwareMap.get(DcMotorEx.class, "launcher_motor_one");
        launcherMotorTwo = hardwareMap.get(DcMotorEx.class, "launcher_motor_two");
        launcherMotorOne.setDirection(DcMotorSimple.Direction.FORWARD);
        indexMotor = hardwareMap.get(DcMotorEx.class, "index_motor");
        indexMotor.setDirection(DcMotorSimple.Direction.FORWARD);
    }

    // Executa o launcher
    public void run(boolean on, boolean shoot, boolean normalPower, boolean maxPower, boolean minPower, boolean launcherIncreasePower, boolean launcherDecreasePower, double improvePower, boolean indexIncreasePower, boolean indexDecreasePower) {

        if (normalPower){
            launcherPowerSelected = normalLauncherSelect;
            indexPowerSelected = normalIndexSelect;
        } else if (minPower) {
            launcherPowerSelected = 0;
            indexPowerSelected = normalIndexSelect;
        } else if (maxPower) {
            launcherPowerSelected = launcherPowers.length - 1;
            indexPowerSelected = 0;
        }

        adjustLauncherPower(launcherIncreasePower, launcherDecreasePower, improvePower);
        adjustIndexPower(indexIncreasePower, indexDecreasePower);

        if (on) {
            onLauncherMotors(launcherPower);
        } else {
            offLauncherMotors();
        }
        if (shoot) {
            onIndexMotor();
        } else {
            offIndexMotor();
        }
    }

    public void adjustLauncherPower(boolean increasePower, boolean decreasePower, double improvePower) {
            if (launcherTimer.milliseconds() > cooldownLauncher) {
                if (increasePower) {
                    launcherPowerSelected = Math.min(launcherPowerSelected + 1, launcherPowers.length - 1);
                    launcherTimer.reset();
                } else if (decreasePower) {
                    launcherPowerSelected = Math.max(launcherPowerSelected - 1, 0);
                    launcherTimer.reset();
                }
            }

        launcherPower = Range.clip(launcherPowers[launcherPowerSelected] + improvePower * 0.07, 0.6, 1);
    }

    public void adjustIndexPower(boolean increasePower, boolean decreasePower) {
        if (indexChangeTimer.milliseconds() > cooldownIndex) {
            if (increasePower) {
                indexPowerSelected = Math.min(indexPowerSelected + 1, indexPowers.length - 1);
            } else if (decreasePower) {
                indexPowerSelected = Math.max(indexPowerSelected - 1, 0);
            }
            indexChangeTimer.reset();
        }

        indexPower = indexPowers[indexPowerSelected];
    }

    public void onIndexMotor() {
        indexMotor.setPower(indexPower);
    }

    public void offIndexMotor() {
        indexMotor.setPower(0);
    }

    public void onLauncherMotors(double power) {
        launcherMotorOne.setPower(power);
        launcherMotorTwo.setPower(power);
    }

    public void offLauncherMotors() {
        launcherMotorOne.setPower(0);
        launcherMotorTwo.setPower(0);
    }

    public void sendTelemetry() {
        telemetry.addData("FlyWheel Power", launcherPower);
        telemetry.addData("Index Power", indexPower);
    }

    public Action onAuto(double flyWheelMaxpower) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    launcherMotorOne.setPower(flyWheelMaxpower);
                    launcherMotorTwo.setPower(flyWheelMaxpower);
                    initialized = true;
                }

                // Mantém rodando até o ParallelAction encerrar
                packet.put("Flywheel", "Active");
                return true;
            }
        };
    }

    public Action launch(double indexAutoPower, double indexAutoSeconds) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                // Inicializa uma vez
                if (!initialized) {
                    indexTimer.reset();
                    indexMotor.setPower(indexAutoPower); // liga o index
                    initialized = true;
                }

                // Enquanto o tempo for menor que 4 segundos, mantém ativo
                double elapsed = indexTimer.seconds();
                packet.put("Index Timer", elapsed);

                if (elapsed < indexAutoSeconds) {
                    return true; // ainda executando
                } else {
                    indexMotor.setPower(0); // desliga após 4s
                    packet.put("Index", "Stopped");
                    return false; // encerra o Action
                }
            }
        };
    }
}
