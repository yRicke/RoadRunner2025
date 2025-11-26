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
            launcher.run(gamepad1.left_trigger > 0, gamepad1.right_trigger > 0, gamepad1.a, gamepad1.b, gamepad1.y);
            launcher.sendTelemetry();
            telemetry.update();
        }
    }
}
