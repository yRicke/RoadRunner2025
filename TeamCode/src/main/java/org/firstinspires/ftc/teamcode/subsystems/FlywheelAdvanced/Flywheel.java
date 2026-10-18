package org.firstinspires.ftc.teamcode.subsystems.FlywheelAdvanced;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Flywheel {
    private final DcMotorEx motor_one;
    private final DcMotorEx motor_two;
    private final Telemetry telemetry;
    private double targetRPM = 0.0;

    private double encoderCPM = 28;

    private double gearRatio = 1;
    private double kS = 0.043, kV = 0.000146, kP = 0.0021;

    public Flywheel(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;
        motor_one = hardwareMap.get(DcMotorEx.class, "launcher_motor_one");
        motor_two = hardwareMap.get(DcMotorEx.class, "launcher_motor_two");
        motor_one.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor_two.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor_one.setDirection(DcMotorSimple.Direction.REVERSE);
        motor_two.setDirection(DcMotorSimple.Direction.FORWARD);
        stopMotors();
    }

    public void setMotorPower(double power) {
        motor_one.setPower(power);
        motor_two.setPower(power);
    }

    public void stopMotors() {
        targetRPM = 0.0;
        motor_one.setPower(0);
        motor_two.setPower(0);
    }

    /** Chame a cada iteração do TeleOp ou Auto. RPM zero ou inválido desliga o motor. */
    public void run(double targetRpm) {
        if (!Double.isFinite(targetRpm) || targetRpm <= 0.0) {
            stopMotors();
            return;
        }

        targetRPM = targetRpm;
        double error = targetRpm - getRPM();
        double ff = (kV * targetRpm) + kS;
        double fb = error * kP;
        double power = ff + fb;

        setMotorPower(power);
    }

    public double getTicksPerSec() {
        return motor_one.getVelocity();
    }

    public double getRPM() {
        return ((getTicksPerSec() / encoderCPM) * 60) / gearRatio;
    }

    public void sendTelemetry() {
        double currentRPM = getRPM();
        telemetry.addData("Flywheel RPM alvo", targetRPM);
        telemetry.addData("Flywheel RPM medido", currentRPM);
        telemetry.addData("Flywheel erro RPM", targetRPM - currentRPM);
        telemetry.addData("Flywheel potência", motor_one.getPower());
    }
}
