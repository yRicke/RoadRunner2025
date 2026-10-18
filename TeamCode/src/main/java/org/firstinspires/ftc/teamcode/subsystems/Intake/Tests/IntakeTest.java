package org.firstinspires.ftc.teamcode.subsystems.Intake.Tests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.subsystems.Intake.Intake;

@TeleOp(name = "Intake Test", group = "Intake")
public class IntakeTest extends LinearOpMode {

    private Intake intake;

    @Override
    public void runOpMode() {
        intake = new Intake(hardwareMap, telemetry);

        waitForStart();
        while (opModeIsActive()) {
            intake.run(gamepad1.left_bumper);
            intake.sendTelemetry();
            telemetry.update();
        }
    }
}
