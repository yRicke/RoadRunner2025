package org.firstinspires.ftc.teamcode.subsystems.Trigger.Tests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.subsystems.Trigger.Trigger;

@TeleOp(name = "Trigger Test", group = "Trigger")
public class TriggerTest extends LinearOpMode {

    private Trigger trigger;

    @Override
    public void runOpMode() {
        trigger = new Trigger(hardwareMap, telemetry);
        trigger.setTriggerClosed();

        waitForStart();
        while (opModeIsActive()) {
            trigger.run(gamepad1.right_bumper);
            trigger.sendTelemetry();
            telemetry.update();
        }
    }
}
