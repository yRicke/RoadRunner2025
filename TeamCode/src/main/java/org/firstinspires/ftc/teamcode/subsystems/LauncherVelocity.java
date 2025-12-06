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
    private final ElapsedTime indexChangeTimer = new ElapsedTime();

    private final Telemetry telemetry;

    // ----- INDEX POWER -----
    double[] indexPowers = {0.4, 0.45, 0.5, 0.55, 0.6, 0.65, 0.7, 0.75, 0.8};
    final int normalIndexSelect = 7;
    int indexPowerSelected = normalIndexSelect;
    long cooldownIndex = 200;
    private double indexPower = indexPowers[indexPowerSelected];

    // ----- VELOCITY CONTROL -----
    private final double TPR = 28; // ticks por rev
    private final double MAX_RPM = 6000; // RPM máxima da flywheel (ajuste se necessário)

    private double rpmMultiplier = 0.7;  // 70% = modo normal
    private double targetRPM = MAX_RPM * rpmMultiplier;
    private double targetVelocityTPS = (targetRPM * TPR / 60.0);

    // minimo de força pra lançar
    private final double SHOOT_THRESHOLD = 0.95; // 95%

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
            boolean minPower,
            boolean indexIncreasePower,
            boolean indexDecreasePower
    ) {

        // ====== RPM PRESET ======
        if (normalPower) {
            rpmMultiplier = 0.7; // 4200 RPM
            indexPowerSelected = normalIndexSelect;
        } else if (minPower) {
            rpmMultiplier = 0.6; // 3600 RPM
            indexPowerSelected = normalIndexSelect;
        } else if (maxPower) {
            rpmMultiplier = 0.9; // 5400 RPM
            indexPowerSelected = normalIndexSelect;
        }

        targetRPM = MAX_RPM * rpmMultiplier;
        targetVelocityTPS = targetRPM * (TPR / 60.0);

        // ===== INDEX POWER ADJUST =====
        adjustIndexPower(indexIncreasePower, indexDecreasePower);

        // ===== LAUNCHER =====
        if (on) {
            onLauncherMotors();
        } else {
            offLauncherMotors();
        }

        // ===== LAUNCH CONTROL ===== lança apenas se o RPM estiver ok
        if (shootRequest && readyToShoot()) {
            onIndexMotor();
        } else {
            offIndexMotor();
        }
    }

    // Verifica se RPM está >= 95% do alvo
    public boolean readyToShoot() {
        double vel1 = launcherMotorOne.getVelocity();
        double vel2 = launcherMotorTwo.getVelocity();
        double avg = (vel1 + vel2) / 2.0;

        return avg >= targetVelocityTPS * SHOOT_THRESHOLD;
    }

    // Ajuste da força do index
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

    // ===== TELEMETRY =====
    public void sendTelemetry() {
        double vel1 = launcherMotorOne.getVelocity();
        double vel2 = launcherMotorTwo.getVelocity();
        double avg = (vel1 + vel2) / 2.0;

        telemetry.addData("Target RPM", targetRPM);
        telemetry.addData("Target TPS", targetVelocityTPS);
        telemetry.addData("Actual TPS M1", vel1);
        telemetry.addData("Actual TPS M2", vel2);
        telemetry.addData("AVG TPS", avg);
        telemetry.addData("Ready to Shoot?", readyToShoot());
        telemetry.addData("Index Power", indexPower);
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
