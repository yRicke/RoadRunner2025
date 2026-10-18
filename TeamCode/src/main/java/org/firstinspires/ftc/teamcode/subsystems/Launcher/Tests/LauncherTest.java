package org.firstinspires.ftc.teamcode.subsystems.Launcher.Tests;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.subsystems.Launcher.LauncherPower;
@Disabled
@TeleOp(name = "Launcher Power Test", group = "Launcher")
public class LauncherTest extends LinearOpMode {

    private LauncherPower launcher;

    @Override
    public void runOpMode() {
        launcher = new LauncherPower(hardwareMap, telemetry);

        waitForStart();
        while (opModeIsActive()) {
            launcher.run(true, gamepad1.right_trigger > 0, gamepad2.a, gamepad2.b, gamepad2.y, gamepad2.right_bumper, gamepad2.left_bumper, gamepad2.right_trigger, gamepad2.dpad_up, gamepad2.dpad_down);
            launcher.sendTelemetry();
            telemetry.update();
        }
    }
}
