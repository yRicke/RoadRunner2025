    package org.firstinspires.ftc.teamcode.TeleOp;

    import com.qualcomm.robotcore.eventloop.opmode.Disabled;
    import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
    import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
    import com.qualcomm.robotcore.hardware.Servo;

    import org.firstinspires.ftc.teamcode.subsystems.Drive;
    import org.firstinspires.ftc.teamcode.subsystems.Intake;
    import org.firstinspires.ftc.teamcode.subsystems.LauncherPIDado;
    import org.firstinspires.ftc.teamcode.subsystems.LauncherPower;

    @TeleOp(name="TeleOpMainBrabo", group="TeleOpMode")

    public class TeleOpMainBrabo extends LinearOpMode {

        private Drive drive;
        private Intake intake;
        private LauncherPIDado launcher;

        @Override
        public void runOpMode() {
            drive = new Drive(hardwareMap, telemetry);
            intake = new Intake(hardwareMap, telemetry);
            launcher = new LauncherPIDado(hardwareMap, telemetry);

            telemetry.addLine("Status: Inicializado");
            telemetry.update();

            waitForStart();

            while (opModeIsActive()) {

                //Subsistemas
                drive.run(gamepad1.left_stick_y, -gamepad1.left_stick_x, gamepad1.right_stick_x, gamepad1.left_trigger > 0, gamepad2.right_stick_x);
                intake.run(true, gamepad1.left_bumper);
                launcher.run(2700);
                // Telemetry Organizado
                drive.sendTelemetry();
                intake.sendTelemetry();
                launcher.sendTelemetry();
                telemetry.update();
            }
        }
    }