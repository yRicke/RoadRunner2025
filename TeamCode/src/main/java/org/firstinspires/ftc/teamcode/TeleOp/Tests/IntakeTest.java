package org.firstinspires.ftc.teamcode.TeleOp.Tests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.subsystems.Intake;

@TeleOp(name="Intake", group="TeleOpMode")
public class IntakeTest extends LinearOpMode {

    private Intake intake;

    @Override
    public void runOpMode() {
        intake = new Intake(hardwareMap, telemetry);

        waitForStart();
        while (opModeIsActive()) {
            intake.run(true, gamepad1.left_bumper);
            intake.sendTelemetry();
            telemetry.update();
        }
    }
}
