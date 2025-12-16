    package org.firstinspires.ftc.teamcode.TeleOp;

    import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
    import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
    import com.qualcomm.robotcore.hardware.Servo;

    import org.firstinspires.ftc.teamcode.subsystems.Drive;
    import org.firstinspires.ftc.teamcode.subsystems.Intake;
    import org.firstinspires.ftc.teamcode.subsystems.LauncherVelocity;

    @TeleOp(name="TeleOpVelocityTest", group="TeleOpMode")
    public class TeleOpVelocityTest extends LinearOpMode {

        private Drive drive;
        private Intake intake;
        private LauncherVelocity launcher;
        Servo rgb;

        @Override
        public void runOpMode() {
            drive = new Drive(hardwareMap, telemetry);
            intake = new Intake(hardwareMap, telemetry);
            launcher = new LauncherVelocity(hardwareMap, telemetry);
            rgb = hardwareMap.get(Servo.class, "rgb");
            rgb.setPosition(0.28);

            telemetry.addLine("Status: Inicializado");
            telemetry.update();

            waitForStart();

            while (opModeIsActive()) {

                //Subsistemas
                drive.run(gamepad1.left_stick_y, -gamepad1.left_stick_x, gamepad1.right_stick_x, gamepad1.left_trigger > 0, gamepad2.right_stick_x);
                intake.run(true, gamepad1.left_bumper);
                launcher.run(true, gamepad1.right_trigger > 0, gamepad2.a, gamepad2.b, gamepad2.y);

                // Telemetry Organizado
                drive.sendTelemetry();
                intake.sendTelemetry();
                launcher.sendTelemetry();
                telemetry.update();
            }
        }
    }