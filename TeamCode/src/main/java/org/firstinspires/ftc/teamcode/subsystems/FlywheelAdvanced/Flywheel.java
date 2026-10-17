package org.firstinspires.ftc.teamcode.subsystems.FlywheelAdvanced;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Flywheel {
    private DcMotorEx m1;

    private double encoderCPM = 28;

    private double gearRatio = 3;
    private double kS = 0.16, kV = 0.000488, kP = 0.0009;

    public void init(HardwareMap hwMap) {
        m1 = hwMap.get(DcMotorEx.class, "leftMotor");
        m1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void setMotorPower(double power) {
        m1.setPower(power);
    }

    public void stopMotors() {
        m1.setPower(0);
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
        return m1.getVelocity();
    }

    public double getRPM() {
        return ((getTicksPerSec() / encoderCPM) * 60) / gearRatio;
    }
}
