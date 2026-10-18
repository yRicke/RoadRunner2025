package org.firstinspires.ftc.teamcode.subsystems.FlywheelAdvanced.Tunning;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.subsystems.FlywheelAdvanced.Flywheel;

@TeleOp(name = "kS Tuner", group = "Flywheel")
public class kSTuner extends OpMode {
    private Flywheel flywheel;

    public static double kS = 0.043;

    double[] increments = {0.000001, 0.00001, 0.0001, 0.001, 0.01};
    int incrementIdx = 4; // Passo inicial: 0.01

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

        double currentStep = increments[incrementIdx];

        // change kS
        if (gamepad1.dpadUpWasPressed()) { kS += currentStep; }
        if (gamepad1.dpadDownWasPressed()) { kS -= currentStep; }

        flywheel.setMotorPower(kS);

        telemetry.addData("Step", "%.6f", currentStep);
        telemetry.addData("kS", "%.6f", kS);
        telemetry.addData("RPM", flywheel.getRPM());
        telemetry.addData("Ticks per Sec", flywheel.getTicksPerSec());
    }
}
