package org.firstinspires.ftc.teamcode.subsystems.FlywheelAdvanced.Tunning;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.subsystems.FlywheelAdvanced.Flywheel;

@TeleOp(name = "kP Tuner", group = "Flywheel")
public class kPTuner extends OpMode {
    private Flywheel flywheel;

    public static double kV = 0.000146;
    public double kS = 0.043;
    public double kP = 0.0021;
    public double goalRPM = 1500;

    double[] increments = {0.000001, 0.00001, 0.0001, 0.001, 0.01};
    int incrementIdx = 4; // Start at 0.001

    @Override
    public void init() {
        flywheel = new Flywheel(hardwareMap, telemetry);
    }

    @Override
    public void loop() {
        if (gamepad1.dpadRightWasPressed() && incrementIdx < 4) {
            incrementIdx++;
        } else if (gamepad1.dpadLeftWasPressed() && incrementIdx > 0) {
            incrementIdx--;
        }

        if (gamepad1.a) {
            goalRPM = 1500;
        }
        else if (gamepad1.b) {
            goalRPM = 800;
        }

        double currentStep = increments[incrementIdx];

        // change kP
        if (gamepad1.dpadUpWasPressed()) { kP += currentStep; }
        if (gamepad1.dpadDownWasPressed()) { kP -= currentStep; }

        double feedForward = (kV * goalRPM) + kS;
        double error = goalRPM - flywheel.getRPM();
        double feedBack = error * kP;

        flywheel.setMotorPower(feedForward + feedBack);

        telemetry.addData("Step", "%.6f", currentStep);
        telemetry.addData("kP", "%.6f", kP);
        telemetry.addData("Error", error);
        telemetry.addData("RPM", flywheel.getRPM());
    }
}
