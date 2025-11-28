package org.firstinspires.ftc.teamcode.TeleOp.Tests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.subsystems.Launcher;

@TeleOp(name="Launcher", group="TeleOpMode")
public class LauncherTest extends LinearOpMode {

    private Launcher launcher;

    @Override
    public void runOpMode() {
        launcher = new Launcher(hardwareMap, telemetry);

        waitForStart();
        while (opModeIsActive()) {
            launcher.run(gamepad1.left_trigger > 0, gamepad2.right_trigger > 0, gamepad2.a, gamepad2.right_bumper, gamepad2.left_bumper, gamepad2.right_trigger);
            launcher.sendTelemetry();
            telemetry.update();
        }
    }
}
