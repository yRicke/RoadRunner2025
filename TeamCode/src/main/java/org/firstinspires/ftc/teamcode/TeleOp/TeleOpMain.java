    package org.firstinspires.ftc.teamcode.TeleOp;

    import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
    import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

    import org.firstinspires.ftc.teamcode.subsystems.AprilTagWebcam;
    import org.firstinspires.ftc.teamcode.subsystems.Drive;
    import org.firstinspires.ftc.teamcode.subsystems.Intake;
    import org.firstinspires.ftc.teamcode.subsystems.Launcher;
    import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

    @TeleOp(name="TeleOpMain", group="TeleOpMode")
    public class TeleOpMain extends LinearOpMode {

        private Drive drive;
        private Intake intake;
        private Launcher launcher;
        private AprilTagWebcam aprilTag;

        @Override
        public void runOpMode() {
            drive = new Drive(hardwareMap, telemetry);
            intake = new Intake(hardwareMap, telemetry);
            launcher = new Launcher(hardwareMap, telemetry);
            aprilTag = new AprilTagWebcam(hardwareMap, telemetry);

            telemetry.addLine("Status: Inicializado");
            telemetry.update();

            waitForStart();

            while (opModeIsActive()) {
                aprilTag.run(); // atualiza leituras

                AprilTagDetection tag20 = aprilTag.getTagByID(20);
                double distance = aprilTag.getRange(tag20);

                // subsistemas
                drive.run(gamepad1.left_stick_y, -gamepad1.left_stick_x, gamepad1.right_stick_x, gamepad1.left_trigger > 0);
                intake.run(gamepad1.left_trigger > 0, gamepad1.left_bumper);
                launcher.run(gamepad1.left_trigger > 0, gamepad1.right_trigger > 0, gamepad1.a, gamepad1.b, gamepad1.y, distance);

                // Telemetry organizado
                aprilTag.sendTelemetry(tag20);
                drive.sendTelemetry();
                intake.sendTelemetry();
                launcher.sendTelemetry();
                telemetry.update();
            }

            aprilTag.stop();
        }
    }