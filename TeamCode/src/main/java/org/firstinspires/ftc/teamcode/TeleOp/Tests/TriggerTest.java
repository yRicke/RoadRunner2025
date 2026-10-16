package org.firstinspires.ftc.teamcode.TeleOp.Tests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.subsystems.Trigger;

@TeleOp(name = "TriggerTest", group = "TeleOpMode")
public class TriggerTest extends LinearOpMode {

    private Trigger trigger;

    @Override
    public void runOpMode() {
        trigger = new Trigger(hardwareMap, telemetry);
        trigger.setTriggerClosed();

        try {
            telemetry.addLine("Segure RB do gamepad 1 para abrir; solte para fechar.");
            trigger.sendTelemetry();
            telemetry.update();

            waitForStart();

            while (opModeIsActive()) {
                trigger.run(gamepad1.right_bumper);
                telemetry.addLine("Segure RB do gamepad 1 para abrir; solte para fechar.");
                trigger.sendTelemetry();
                telemetry.update();
                idle();
            }
        } finally {
            trigger.setTriggerClosed();
        }
    }
}
