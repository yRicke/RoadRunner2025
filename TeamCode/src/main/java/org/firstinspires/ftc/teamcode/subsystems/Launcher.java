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

public class Launcher {

    private final DcMotorEx launcherMotorOne;
    private final DcMotorEx launcherMotorTwo;
    private final DcMotorEx indexMotor;

    private final ElapsedTime indexTimer = new ElapsedTime();
    private final double indexAutoSeconds = 2.4;

    private final Telemetry telemetry;

    double[] launcherPowers = {0.6, 0.65, 0.70, 0.75, 0.80, 0.85, 0.9,0.95, 1.0};
    double[] indexPowers = {0.5, 0.6, 0.7, 0.75, 0.8, 0.85, 0.9, 0.95, 1.0};
    int indexPowerSelected = 4;
    int launcherPowerSelected = 3;
    long lastLauncherChange = 0;
    long lastIndexChange = 0;
    long cooldownLauncher = 200;// 200ms
    long cooldownIndex = 200;// 200ms

    private double launcherPower = launcherPowers[launcherPowerSelected];
    private double indexPower = indexPowers[indexPowerSelected];
    private final double indexPowerAuto = 0.46;


    // Construtor recebe o hardwareMap e telemetry do OpMode
    public Launcher(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;

        launcherMotorOne = hardwareMap.get(DcMotorEx.class, "launcher_motor_one");
        launcherMotorTwo = hardwareMap.get(DcMotorEx.class, "launcher_motor_two");
        launcherMotorOne.setDirection(DcMotorSimple.Direction.REVERSE);
        indexMotor = hardwareMap.get(DcMotorEx.class, "index_motor");
        indexMotor.setDirection(DcMotorSimple.Direction.REVERSE);

    }

    // Executa o launcher
    public void run(boolean on, boolean shoot, boolean normalPower, boolean maxPower, boolean minPower, boolean launcherIncreasePower, boolean launcherDecreasePower, double improvePower, boolean indexIncreasePower, boolean indexDecreasePower) {

        long now = System.currentTimeMillis();

        // Ajuste da velocidade do launcher
        adjustLauncherPower(now, launcherIncreasePower, launcherDecreasePower, normalPower, minPower, maxPower, improvePower);
        // Ajuste da velocidade do index
        adjustIndexPower(now, indexIncreasePower, indexDecreasePower);

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

    public void adjustLauncherPower(long now, boolean increasePower, boolean decreasePower, boolean normalPower, boolean minPower, boolean maxPower, double improvePower) {

        if(normalPower){
            launcherPowerSelected = 3;
        } else if(minPower){
            launcherPowerSelected = 0;
        } else if(maxPower){
            launcherPowerSelected = launcherPowers.length -1;
        } else {
            if (now - lastLauncherChange > cooldownLauncher) {
                if (increasePower) {
                    launcherPowerSelected = Math.min(launcherPowerSelected + 1, launcherPowers.length - 1);
                    lastLauncherChange = now;
                } else if (decreasePower) {
                    launcherPowerSelected = Math.max(launcherPowerSelected - 1, 0);
                    lastLauncherChange = now;
                }
            }
        }
        launcherPower = Range.clip(launcherPowers[launcherPowerSelected] + improvePower * 0.05, 0.6, 1);
    }

    public void adjustIndexPower(long now, boolean increasePower, boolean decreasePower) {
        if (now - lastIndexChange > cooldownIndex) {
            if (increasePower) {
                indexPowerSelected = Math.min(indexPowerSelected + 1, indexPowers.length - 1);
                lastIndexChange = now;
            } else if (decreasePower) {
                indexPowerSelected = Math.max(indexPowerSelected - 1, 0);
                lastIndexChange = now;
            }
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
        telemetry.addData("Index Power", indexMotor.getPower());
    }

    public Action onAuto(double flyWheelMaxpower) {
        return new Action() {
            private boolean initialized = false;
            final double flyWheelMaxPowerAuto = flyWheelMaxpower;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    launcherMotorOne.setPower(-flyWheelMaxPowerAuto);
                    launcherMotorTwo.setPower(flyWheelMaxPowerAuto);
                    initialized = true;
                }

                // Mantém rodando até o ParallelAction encerrar
                packet.put("Flywheel", "Active");
                return true;
            }
        };
    }

    public Action launch() {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                // Inicializa uma vez
                if (!initialized) {
                    indexTimer.reset();
                    indexMotor.setPower(indexPowerAuto); // liga o index
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
