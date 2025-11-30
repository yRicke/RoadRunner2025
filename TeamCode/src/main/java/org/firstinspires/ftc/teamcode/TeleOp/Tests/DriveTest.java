package org.firstinspires.ftc.teamcode.TeleOp.Tests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.subsystems.Drive;

@TeleOp(name="DriveTest", group="TeleOpMode")
public class DriveTest extends LinearOpMode {

    private Drive drive;

    @Override
    public void runOpMode() {
        drive = new Drive(hardwareMap, telemetry);

        waitForStart();
        while (opModeIsActive()) {
            drive.run(gamepad1.left_stick_y, -gamepad1.left_stick_x, gamepad1.right_stick_x, gamepad1.right_bumper, gamepad2.right_stick_x);
            drive.sendTelemetry();
            telemetry.update();
        }
    }
}
