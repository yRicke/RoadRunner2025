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

    private final ElapsedTime recoveryTimer = new ElapsedTime();

    private final Telemetry telemetry;

    // ----- INDEX POWER -----
    private final double indexPower = 0.9;

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

    public void run(boolean on, boolean shootRequest, boolean normalPower, boolean maxPower, boolean minPower) {

        // ===== PRESETS DE RPM =====
        launcherSetMode(normalPower, maxPower, minPower);

        // ===== SE NÃO RECUPERANDO, DEFINE O TARGET NORMAL =====
        setNormalTPS();

        // ===== ATIVA OU DESATIVA O FLYWHEEL =====
        setLauncherOn(on);

        // ===== CONTROLE DE DISPARO =====
        shootControl(shootRequest);

        // ===== SISTEMA DE RECUPERAÇÃO DE RPM =====
        recoveryRPMsystem();

        // ===== GARANTE TARGET NORMAL SE NÃO ESTIVER RECUPERANDO =====
        setNormalTPS();

    }
    public void setLauncherOn(boolean on){
        if (on) onLauncherMotors();
        else offLauncherMotors();
    }
    public void recoveryRPMsystem() {
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
    public void setNormalTPS(){
        // Se não estiver recuperando, define target normal
        if (!recovering) {
            targetRPM = MAX_RPM * rpmMultiplier;
            targetVelocityTPS = targetRPM * (TPR / 60.0);
        }
    }

    public void shootControl(boolean shootRequest) {
        // ===== CONTROLE DE DISPARO =====
        if (shootRequest && readyToShoot()) {

            // inicia recuperação pós-disparo
            recovering = true;
            recoveryTimer.reset();

            onIndexMotor();

        } else {
            offIndexMotor();
        }
    }
    public void launcherSetMode(boolean normalPower, boolean maxPower, boolean minPower) {
        // ===== PRESETS DE RPM =====
        if (normalPower) {
            rpmMultiplier = 0.48;
        } else if (minPower) {
            rpmMultiplier = 0.45;
        } else if (maxPower) {
            rpmMultiplier = 0.605;
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
    public Action onAuto(boolean normal, boolean max, boolean min) {
        return packet -> {
            run(true, false, normal, max, min);
            packet.put("Launcher Avg TPS", (launcherMotorOne.getVelocity() + launcherMotorTwo.getVelocity()) / 2.0);
            return true;
        };
    }

    public Action launchAuto(int shotIndex, int totalShots) {

        return new Action() {

            private boolean initialized = false;
            private boolean dropDetected = false;
            private boolean recovered = false;

            private double baseVelocity = 0;
            private final double DROP_THRESHOLD = 0.80;   // Queda grande → bola entrou
            private final double RECOVER_THRESHOLD = 0.92; // Recuperou → bola saiu

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {

                double vel1 = launcherMotorOne.getVelocity();
                double vel2 = launcherMotorTwo.getVelocity();
                double avg = (vel1 + vel2) / 2.0;

                if (!initialized) {
                    // mede a velocidade inicial sem bola
                    baseVelocity = targetVelocityTPS;

                    // liga index
                    indexMotor.setPower(0.9);

                    initialized = true;
                }

                // -------- DETECTA QUEDA (bola entrando) --------
                if (!dropDetected && avg < baseVelocity * DROP_THRESHOLD) {
                    dropDetected = true;
                }

                // -------- DETECTA RECUPERAÇÃO COMPLETA --------
                if (dropDetected && avg >= baseVelocity * RECOVER_THRESHOLD) {
                    recovered = true;
                }

                packet.put("AvgVel", avg);
                packet.put("DropDetected", dropDetected);
                packet.put("Recovered", recovered);

                // -------- QUANDO A BOLA SAI --------
                if (recovered) {

                    indexMotor.setPower(0);

                    // ---- Se essa é a última bola ----
                    if (shotIndex == totalShots - 1) {
                        return false; // ENCERRA O ACTION
                    }

                    return false; // termina esse cycle e o autonômo segue para o próximo
                }

                return true; // continua rodando
            }
        };
    }
}
