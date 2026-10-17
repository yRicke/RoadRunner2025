package org.firstinspires.ftc.teamcode.TeleOp.Tests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.subsystems.Trigger;

@TeleOp(name = "TriggerPosition", group = "TeleOpMode")
public class TriggerPosition extends LinearOpMode {

    private Trigger trigger;

    @Override
    public void runOpMode() {
        trigger = new Trigger(hardwareMap, telemetry);

        waitForStart();
        while (opModeIsActive()) {
            trigger.runConfig(gamepad1.dpad_up, gamepad1.dpad_down);
            trigger.sendTelemetry();
            telemetry.update();
        }
    }
}
