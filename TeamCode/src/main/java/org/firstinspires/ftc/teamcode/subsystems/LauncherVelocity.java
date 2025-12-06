package org.firstinspires.ftc.teamcode.subsystems;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import com.acmerobotics.roadrunner.Action;

public class LauncherVelocity {

    private final DcMotorEx launcherMotorOne;
    private final DcMotorEx launcherMotorTwo;
    private final DcMotorEx indexMotor;

    private final ElapsedTime indexTimer = new ElapsedTime();
    private final ElapsedTime recoveryTimer = new ElapsedTime();

    private final Telemetry telemetry;

    // ----- INDEX POWER -----
    private double indexPower = 0.9;

    // ----- VELOCITY CONTROL -----
    private final double TPR = 28; // ticks por rev
    private final double MAX_RPM = 6000; // ajuste de acordo com seu motor

    // Presets
    private double rpmMultiplier = 0.45; // modo normal
    private double targetRPM = MAX_RPM * rpmMultiplier;
    private double targetVelocityTPS = (targetRPM * TPR / 60.0);

    // ----- SHOOT CHECK -----
    private final double SHOOT_THRESHOLD = 0.9; // 8 0% da velocidade alvo

    // ----- RPM RECOVERY -----
    private boolean recovering = false;
    private final double RECOVERY_TIME = 0.18; // 180ms
    private final double RECOVERY_DROP = 0.94; // 94% do alvo
    public LauncherVelocity(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;

        launcherMotorOne = hardwareMap.get(DcMotorEx.class, "launcher_motor_one");
        launcherMotorTwo = hardwareMap.get(DcMotorEx.class, "launcher_motor_two");

        launcherMotorOne.setDirection(DcMotorSimple.Direction.REVERSE);
        launcherMotorTwo.setDirection(DcMotorSimple.Direction.FORWARD);

        indexMotor = hardwareMap.get(DcMotorEx.class, "index_motor");
        indexMotor.setDirection(DcMotorSimple.Direction.FORWARD);

        launcherMotorOne.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        launcherMotorTwo.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
    }

    public void run(
            boolean on,
            boolean shootRequest,
            boolean normalPower,
            boolean maxPower,
            boolean minPower
    ) {

        // ===== PRESETS DE RPM =====
        if (normalPower) {
            rpmMultiplier = 0.48;
        } else if (minPower) {
            rpmMultiplier = 0.45;
        } else if (maxPower) {
            rpmMultiplier = 0.605;
        }

        // Se não estiver recuperando, define target normal
        if (!recovering) {
            targetRPM = MAX_RPM * rpmMultiplier;
            targetVelocityTPS = targetRPM * (TPR / 60.0);
        }

        // ===== ATIVA OU DESATIVA FLYWHEEL =====
        if (on) onLauncherMotors();
        else offLauncherMotors();

        // ===== CONTROLE DE DISPARO =====
        if (shootRequest && readyToShoot()) {

            // inicia recuperação pós-disparo
            recovering = true;
            recoveryTimer.reset();

            onIndexMotor();

        } else {
            offIndexMotor();
        }

        // ===== SISTEMA DE RECUPERAÇÃO DE RPM =====
        if (recovering) {
            if (recoveryTimer.seconds() < RECOVERY_TIME) {

                // Reduz temporariamente o RPM para evitar overshoot
                double reducedRPM = (MAX_RPM * rpmMultiplier) * RECOVERY_DROP;
                targetVelocityTPS = reducedRPM * (TPR / 60.0);

            } else {

                // Terminou a recuperação
                recovering = false;
                targetRPM = MAX_RPM * rpmMultiplier;
                targetVelocityTPS = targetRPM * (TPR / 60.0);
            }
        }
    }

    public boolean readyToShoot() {
        double vel1 = launcherMotorOne.getVelocity();
        double vel2 = launcherMotorTwo.getVelocity();
        double avg = (vel1 + vel2) / 2.0;
        return avg >= targetVelocityTPS * SHOOT_THRESHOLD;
    }

    // ===== INDEX =====
    public void onIndexMotor() {
        indexMotor.setPower(indexPower);
    }

    public void offIndexMotor() {
        indexMotor.setPower(0);
    }

    // ===== LAUNCHER =====
    public void onLauncherMotors() {
        launcherMotorOne.setVelocity(targetVelocityTPS);
        launcherMotorTwo.setVelocity(targetVelocityTPS);
    }

    public void offLauncherMotors() {
        launcherMotorOne.setPower(0);
        launcherMotorTwo.setPower(0);
    }

    // ===== TELEMETRIA =====
    public void sendTelemetry() {
        double vel1 = launcherMotorOne.getVelocity();
        double vel2 = launcherMotorTwo.getVelocity();
        double avg = (vel1 + vel2) / 2.0;

        telemetry.addData("Target RPM", targetRPM);
        telemetry.addData("Target TPS", targetVelocityTPS);
        telemetry.addData("AVG TPS", avg);
        telemetry.addData("Recovering?", recovering);
        telemetry.addData("Ready to Shoot?", readyToShoot());
    }

    // ----- AUTO ACTIONS -----
    public Action onAuto(double flyWheelPower) {
        return packet -> {
            launcherMotorOne.setPower(flyWheelPower);
            launcherMotorTwo.setPower(flyWheelPower);
            packet.put("Flywheel", "Active");
            return true;
        };
    }

    public Action launch(double indexAutoPower, double indexAutoSeconds) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    indexTimer.reset();
                    indexMotor.setPower(indexAutoPower);
                    initialized = true;
                }

                double elapsed = indexTimer.seconds();
                packet.put("Index Timer", elapsed);

                if (elapsed < indexAutoSeconds) {
                    return true;
                } else {
                    indexMotor.setPower(0);
                    return false;
                }
            }
        };
    }
}
