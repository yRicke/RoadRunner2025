    package org.firstinspires.ftc.teamcode.TeleOp;

    import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
    import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

    import org.firstinspires.ftc.teamcode.subsystems.Drive;
    import org.firstinspires.ftc.teamcode.subsystems.Intake;
    import org.firstinspires.ftc.teamcode.subsystems.Launcher;

    @TeleOp(name="TeleOpMain", group="TeleOpMode")
    public class TeleOpMain extends LinearOpMode {

        private Drive drive;
        private Intake intake;
        private Launcher launcher;

        @Override
        public void runOpMode() {
            drive = new Drive(hardwareMap, telemetry);
            intake = new Intake(hardwareMap, telemetry);
            launcher = new Launcher(hardwareMap, telemetry);

            telemetry.addLine("Status: Inicializado");
            telemetry.update();

            waitForStart();

            while (opModeIsActive()) {

                // subsistemas
                drive.run(gamepad1.left_stick_y, -gamepad1.left_stick_x, gamepad1.right_stick_x, gamepad1.right_bumper, gamepad2.right_stick_x, gamepad2.right_trigger>0);
                intake.run(gamepad1.left_trigger > 0, gamepad1.left_bumper);
                launcher.run(gamepad1.left_trigger > 0, gamepad2.right_trigger > 0, gamepad2.a, gamepad2.right_bumper, gamepad2.left_bumper, gamepad2.right_trigger);

                // Telemetry organizado
                drive.sendTelemetry();
                intake.sendTelemetry();
                launcher.sendTelemetry();
                telemetry.update();
            }
        }
    }