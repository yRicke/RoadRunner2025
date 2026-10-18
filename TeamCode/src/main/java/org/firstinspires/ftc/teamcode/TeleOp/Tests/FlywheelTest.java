package org.firstinspires.ftc.teamcode.TeleOp.Tests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.subsystems.FlywheelAdvanced.Flywheel;

@TeleOp(name = "Flywheel Test", group = "TeleOpMode")
public class FlywheelTest extends LinearOpMode {

    private static final double TARGET_RPM = 2800.0;

    @Override
    public void runOpMode() {
        Flywheel flywheel = new Flywheel(hardwareMap, telemetry);

        try {
            telemetry.addLine("Segure o gatilho direito do gamepad 1 para ligar a flywheel.");
            telemetry.addLine("Solte o gatilho para desligar.");
            flywheel.sendTelemetry();
            telemetry.update();

            waitForStart();

            while (opModeIsActive()) {
                boolean enabled = gamepad1.right_trigger > 0;
                flywheel.run(enabled ? TARGET_RPM : 0.0);
                telemetry.addData("Flywheel habilitada", enabled);
                flywheel.sendTelemetry();
                telemetry.update();
                idle();
            }
        } finally {
            flywheel.stopMotors();
        }
    }
}
