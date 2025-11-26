package org.firstinspires.ftc.teamcode.Auto;

import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Launcher;

@Autonomous(name = "AutoBlue", group = "Autonomous")
public class AutoBlue extends LinearOpMode {


    // ==========================
    //   POSIÇÕES DOS PONTOS
    // ==========================
    final  double startPositionX = -18;
    final double startPositionY = -18;

    final double ppgPositionX = -12;
    final double ppgPositionY = -55;

    final double pgpPositionX = 12;
    final double pgpPositionY = -55;

    final double gppPositionX = 36;
    final double gppPositionY = -55;

    final double preY = -25;

    final double startPositionHeading = Math.toRadians(45);
    final double modifPositionHeading = Math.toRadians(270);

    Pose2d startPose = new Pose2d(startPositionX,startPositionY, startPositionHeading);

    Vector2d startVector = new Vector2d(startPositionX, startPositionY);

    Vector2d ppgVector = new Vector2d(ppgPositionX, ppgPositionY);
    Vector2d pgpVector = new Vector2d(pgpPositionX, pgpPositionY);
    Vector2d gppVector = new Vector2d(gppPositionX, gppPositionY);

    // Pontos intermediários (x - 34)
    Vector2d prePpgVector= new Vector2d(ppgPositionX, preY);
    Vector2d prePgpVector= new Vector2d(pgpPositionX, preY);
    Vector2d preGppVector = new Vector2d(gppPositionX, preY);

    Intake intake;
    Launcher launcher;

    @Override
    public void runOpMode(){

        intake = new Intake(hardwareMap, telemetry);
        launcher = new Launcher(hardwareMap, telemetry);

        //Odometria
        MecanumDrive drive = new MecanumDrive(hardwareMap, startPose);
        //Trajetórias

        TrajectoryActionBuilder goToPPG = drive.actionBuilder(startPose)
                .strafeToLinearHeading(prePpgVector, modifPositionHeading)
                .strafeTo(ppgVector)
                ;

        TrajectoryActionBuilder goToPGP = drive.actionBuilder(new Pose2d
                        (new Vector2d(startPositionX, startPositionY), startPositionHeading))
                .strafeToLinearHeading(prePgpVector, modifPositionHeading)
                .strafeTo(pgpVector)
                ;

        TrajectoryActionBuilder goToGPP = drive.actionBuilder(new Pose2d
                        (new Vector2d(startPositionX, startPositionY), startPositionHeading))
                .strafeToLinearHeading(preGppVector, modifPositionHeading)
                .strafeTo(gppVector)
                ;

        TrajectoryActionBuilder returnToStartByPPG = drive.actionBuilder(new Pose2d(ppgVector, modifPositionHeading))
                .strafeToLinearHeading
                        (new Vector2d(startPositionX, startPositionY), startPositionHeading)
                ;

        TrajectoryActionBuilder returnToStartByPGP = drive.actionBuilder(new Pose2d(pgpVector, modifPositionHeading))
                .strafeToLinearHeading
                        (new Vector2d(startPositionX, startPositionY), startPositionHeading)
                ;

        TrajectoryActionBuilder returnToStartByGPP = drive.actionBuilder(new Pose2d(gppVector, modifPositionHeading))
                .strafeToLinearHeading
                        (new Vector2d(startPositionX+1, startPositionY), startPositionHeading)
                ;

        // ======================================
// SELEÇÃO DE TENSÃO (antes do start)
// ======================================

        double flywheelPower = 0.665; // padrão (12.8V)
        double[] voltages = {12.6, 12.8, 13.0, 13.2, 13.4};
        double[] powers   = {0.8, 0.77, 0.75, 0.735, 0.72};
        int index = 1; // começa em 12.8V

        telemetry.addLine("=== SELECIONE A VOLTAGEM ===");
        telemetry.update();

// loop de seleção antes de iniciar
        while (!isStarted() && !isStopRequested()) {
            if (gamepad1.dpad_up) {
                index = Math.min(index + 1, voltages.length - 1);
                sleep(200);
            } else if (gamepad1.dpad_down) {
                index = Math.max(index - 1, 0);
                sleep(200);
            }

            telemetry.clear();
            telemetry.addData("Voltagem selecionada", "%.1f V", voltages[index]);
            telemetry.addData("Potência correspondente", "%.3f", powers[index]);
            telemetry.addLine("Use D-Pad ↑↓ para alterar");
            telemetry.update();
        }

        // define potência final conforme a seleção
        flywheelPower = powers[index];

        waitForStart();


        Actions.runBlocking(
                new ParallelAction(
                        intake.onAuto(),
                        launcher.onAuto(flywheelPower),
                        new SequentialAction(
                                new DelayAction(1.8),
                                launcher.launch(),
                                goToPPG.build(),
                                returnToStartByPPG.build(),
                                launcher.launch(),
                                goToPGP.build(),
                                returnToStartByPGP.build(),
                                launcher.launch(),
                                goToGPP.build(),
                                returnToStartByGPP.build(),
                                launcher.launch()
                        )
                )
        );
    }
}