package org.firstinspires.ftc.teamcode.subsystems;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.SleepAction;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Auto.DelayAction;

public class Launcher {

    private DcMotorEx launcherMotorOne = null;
    private DcMotorEx launcherMotorTwo = null;
    private DcMotorEx indexMotor = null;

    private double launcherPower = 0.75;

    private final double indexMaxPower = 1;
    private final double indexMaxPowerAuto = 0.46;

    private ElapsedTime indexTimer = new ElapsedTime();
    private double indexAutoSeconds = 2.4;

    private Telemetry telemetry;


    // Construtor recebe o hardwareMap e telemetry do OpMode
    public Launcher(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;

        launcherMotorOne = hardwareMap.get(DcMotorEx.class, "launcher_motor_one");
        launcherMotorTwo = hardwareMap.get(DcMotorEx.class, "launcher_motor_two");
        indexMotor = hardwareMap.get(DcMotorEx.class, "index_motor");
        indexMotor.setDirection(DcMotorSimple.Direction.REVERSE);

    }

    double[] powers = {0.6, 0.65, 0.70, 0.75, 0.80, 0.85, 0.9,0.95, 1.0};
    int index = 3;
    long lastChange = 0;
    long cooldown = 200; // 200ms

    // Executa o launcher
    public void run(boolean on, boolean shoot, boolean normalPower, boolean increasePower, boolean decreasePower, double improvePower) {

        long now = System.currentTimeMillis();

        if (now - lastChange > cooldown) {
            if (increasePower) {
                index = Math.min(index + 1, powers.length - 1);
                lastChange = now;
            } else if (decreasePower) {
                index = Math.max(index - 1, 0);
                lastChange = now;
            } else if(normalPower){
                index = 3;
                lastChange = now;
            }
        }
        launcherPower = powers[index] +  improvePower*0.075;

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

    public void onIndexMotor() {
        indexMotor.setPower(indexMaxPower);
    }
    public void offIndexMotor() {
        indexMotor.setPower(0);
    }

    public void onLauncherMotors(double power) {
        launcherMotorOne.setPower(-power);
        launcherMotorTwo.setPower(power);
    }

    public void offLauncherMotors() {
        launcherMotorOne.setPower(0);
        launcherMotorTwo.setPower(0);
    }

    public void sendTelemetry() {
        telemetry.addData("Flywheelpower", launcherPower);
        telemetry.addData("Launcher Motor One Power", launcherMotorOne.getPower());
        telemetry.addData("Launcher Motor Two Power", launcherMotorTwo.getPower());
        telemetry.addData("Index Motor Power", indexMotor.getPower());
    }

    public Action onAuto(double flyWheelMaxpower) {
        return new Action() {
            private boolean initialized = false;
            double launcherMaxPowerAuto = flyWheelMaxpower;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    launcherMotorOne.setPower(-launcherMaxPowerAuto);
                    launcherMotorTwo.setPower(launcherMaxPowerAuto);
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
                    indexMotor.setPower(indexMaxPowerAuto); // liga o index
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
