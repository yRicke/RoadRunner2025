package org.firstinspires.ftc.teamcode.TeleOp.Tests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.subsystems.DriveField;

@TeleOp(name="DriveFieldTest", group="TeleOpMode")
public class DriveFieldTest extends LinearOpMode {

    private DriveField drive;

    @Override
    public void runOpMode() {
        drive = new DriveField(hardwareMap, telemetry);

        telemetry.addLine("Field-centric: a orientação no START define a referência do campo.");
        telemetry.addLine("Gamepad1 BACK: redefinir referência do campo.");
        telemetry.addLine("Gamepad1 bumper direito: modo de precisão.");
        telemetry.update();

        waitForStart();
        if (isStopRequested()) {
            return;
        }

        drive.resetYaw();
        boolean backWasPressed = false;

        while (opModeIsActive()) {
            if (gamepad1.back && !backWasPressed) {
                drive.resetYaw();
            }
            backWasPressed = gamepad1.back;

            // Mantém o mapeamento de controles do DriveTest.
            drive.run(gamepad1.left_stick_y, -gamepad1.left_stick_x,
                    gamepad1.right_stick_x, gamepad1.right_bumper, gamepad2.right_stick_x);
            drive.sendTelemetry();
            telemetry.update();
        }
    }
}
