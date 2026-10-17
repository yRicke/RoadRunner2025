package org.firstinspires.ftc.teamcode.subsystems.FlywheelAdvanced;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Flywheel {
    private DcMotorEx motor;

    private double encoderCPM = 28;

    private double gearRatio = 1;
    private double kS = 0.043, kV = 0.000146, kP = 0.0021;

    public void init(HardwareMap hwMap) {
        motor = hwMap.get(DcMotorEx.class, "launcher_motor");
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void setMotorPower(double power) {
        motor.setPower(power);
    }

    public void stopMotors() {
        motor.setPower(0);
    }

    public void setMotorRPM(double targetRpm) {
        // call this every loop
        double error = targetRpm - getRPM();
        double ff = (kV * targetRpm) + kS;
        double fb = error * kP;
        double power = ff + fb;

        setMotorPower(power);
    }

    public double getTicksPerSec() {
        return motor.getVelocity();
    }

    public double getRPM() {
        return ((getTicksPerSec() / encoderCPM) * 60) / gearRatio;
    }
}
