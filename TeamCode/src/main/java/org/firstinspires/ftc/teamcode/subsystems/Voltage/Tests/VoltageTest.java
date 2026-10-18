package org.firstinspires.ftc.teamcode.subsystems.Voltage.Tests;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.subsystems.Voltage.Voltage;
@Disabled
@TeleOp(name = "Voltage Test", group = "Voltage")
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
