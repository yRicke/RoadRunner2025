    package org.firstinspires.ftc.teamcode.TeleOp;

    import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
    import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

    import org.firstinspires.ftc.teamcode.subsystems.Drive.Drive;
    import org.firstinspires.ftc.teamcode.subsystems.FlywheelAdvanced.Flywheel;
    import org.firstinspires.ftc.teamcode.subsystems.Intake.Intake;
    import org.firstinspires.ftc.teamcode.subsystems.Trigger.Trigger;

    @TeleOp(name="TeleOpMainBrabo", group="TeleOpMode")

    public class TeleOpMainBrabo extends LinearOpMode {

        private static final double FLYWHEEL_TARGET_RPM = 2800;

        private Drive drive;
        private Intake intake;
        private Flywheel flywheel;
        private Trigger trigger;

        @Override
        public void runOpMode() {
            drive = new Drive(hardwareMap, telemetry);
            intake = new Intake(hardwareMap, telemetry);
            flywheel = new Flywheel(hardwareMap, telemetry);
            trigger = new Trigger(hardwareMap, telemetry);

            telemetry.addLine("Status: Inicializado");
            telemetry.update();

            try {
                waitForStart();

                while (opModeIsActive()) {

                    //Subsistemas
                    drive.run(gamepad1.left_stick_y, -gamepad1.left_stick_x, gamepad1.right_stick_x, gamepad1.left_trigger > 0, gamepad2.right_stick_x);
                    intake.run(gamepad1.left_bumper);
                    flywheel.run(FLYWHEEL_TARGET_RPM);
                    trigger.run(gamepad1.right_bumper);
                    // Telemetry Organizado
                    drive.sendTelemetry();
                    intake.sendTelemetry();
                    flywheel.sendTelemetry();
                    trigger.sendTelemetry();
                    telemetry.update();
                }
            } finally {
                flywheel.stopMotors();
            }
        }
    }
