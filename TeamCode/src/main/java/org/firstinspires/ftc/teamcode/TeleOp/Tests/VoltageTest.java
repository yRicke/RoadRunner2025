package org.firstinspires.ftc.teamcode.TeleOp.Tests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.subsystems.Voltage;

@TeleOp(name="VoltageTest", group="TeleOpMode")
public class VoltageTest extends LinearOpMode {

    private Voltage voltage;

    @Override
    public void runOpMode() throws InterruptedException {
        voltage = new Voltage(hardwareMap, telemetry);

        waitForStart();
        while (opModeIsActive()) {
            voltage.sendTelemetry();
            telemetry.update();
        }
    }
}
