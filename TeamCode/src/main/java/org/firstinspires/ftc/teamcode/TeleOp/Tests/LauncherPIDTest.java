package org.firstinspires.ftc.teamcode.TeleOp.Tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.subsystems.LauncherPIDado;

@Config
@TeleOp(name = "LauncherPIDTest", group = "TeleOpMode")
public class LauncherPIDTest extends LinearOpMode {

    public static volatile double RPM = 0.0;
    public static volatile boolean ENABLED = false;

    @Override
    public void runOpMode() {
        // Campos estáticos permanecem entre execuções: cada INIT começa desligado.
        ENABLED = false;
        RPM = 0.0;
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        LauncherPIDado launcher = new LauncherPIDado(hardwareMap, telemetry);

        try {
            telemetry.addLine("Dashboard: LauncherPIDTest.RPM e ENABLED controlam o motor.");
            telemetry.addLine("Dashboard: LauncherPIDado contém os ganhos P, I, D e F.");
            launcher.sendTelemetry();
            telemetry.update();

            waitForStart();

            while (opModeIsActive()) {
                launcher.run(ENABLED ? RPM : 0.0);
                telemetry.addData("Launcher habilitado", ENABLED);
                telemetry.addData("Launcher RPM solicitado", RPM);
                launcher.sendTelemetry();
                telemetry.update();
                idle();
            }
        } finally {
            launcher.stop();
        }
    }
}
