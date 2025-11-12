package org.firstinspires.ftc.teamcode.subsystems;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Launcher {

    private DcMotorEx launcherMotorOne = null;
    private DcMotorEx launcherMotorTwo = null;
    private DcMotorEx indexMotor = null;
    private double lastDistance = 0; // guarda a última distância válida


    private double launcherPower = 0.7;
    private double extra = 0;
    private double powerPerDistance = 0.0041975308642;

    private final double indexMaxPower = 1;

    private Telemetry telemetry;
    private ElapsedTime indexTimer = new ElapsedTime();
    private double indexSeconds = 2.5;

    // Construtor recebe o hardwareMap e telemetry do OpMode
    public Launcher(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;

        launcherMotorOne = hardwareMap.get(DcMotorEx.class, "launcher_motor_one");
        launcherMotorTwo = hardwareMap.get(DcMotorEx.class, "launcher_motor_two");
        indexMotor = hardwareMap.get(DcMotorEx.class, "index_motor");

    }

    // Executa o launcher
    public void run(boolean on, boolean shoot, boolean modeA, boolean modeB, boolean modeY, double distance) {
        if (modeA){
            extra = 0;
        } else if(modeB){
            extra = 0.1;
        } else if (modeY){
            extra = 0.2;
        }
        launcherPower= distance * powerPerDistance + extra;

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
                    indexMotor.setPower(indexMaxPower); // liga o index
                    initialized = true;
                }

                // Enquanto o tempo for menor que 4 segundos, mantém ativo
                double elapsed = indexTimer.seconds();
                packet.put("Index Timer", elapsed);

                if (elapsed < indexSeconds) {
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
