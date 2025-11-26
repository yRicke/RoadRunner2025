package org.firstinspires.ftc.teamcode.subsystems;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Launcher {

    private DcMotorEx launcherMotorOne = null;
    private DcMotorEx launcherMotorTwo = null;
    private DcMotorEx indexMotor = null;

    private double launcherPower = 0.8;

    private final double indexMaxPower = 1;

    private Telemetry telemetry;
    private ElapsedTime indexTimer = new ElapsedTime();
    private double indexAutoSeconds = 2.4;

    // Construtor recebe o hardwareMap e telemetry do OpMode
    public Launcher(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;

        launcherMotorOne = hardwareMap.get(DcMotorEx.class, "launcher_motor_one");
        launcherMotorTwo = hardwareMap.get(DcMotorEx.class, "launcher_motor_two");
        indexMotor = hardwareMap.get(DcMotorEx.class, "index_motor");
        indexMotor.setDirection(DcMotorSimple.Direction.REVERSE);

    }

    // Executa o launcher
    public void run(boolean on, boolean shoot, boolean modeA, boolean modeB, boolean modeY) {
        if (modeA){
            launcherPower = 0.8; //Normal
        } else if(modeB){
            launcherPower = 1.0; //Forte
        } else if (modeY){
            launcherPower = 0.7; //Fraco
        }

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
                    indexMotor.setPower(0.46); // liga o index
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
