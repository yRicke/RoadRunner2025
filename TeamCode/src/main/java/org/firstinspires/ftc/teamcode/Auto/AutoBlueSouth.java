package org.firstinspires.ftc.teamcode.Auto;

import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.LauncherPower;
import org.firstinspires.ftc.teamcode.subsystems.Voltage;

/**
 * Autônomo para o lado Azul Sul (Blue South) do campo.
 * O robô começa na posição X=62, Y=-12 e se move para Launch (X=50, Y=-12),
 * e usa Second Launch (X=-12, Y=-12) como ponto de rearmamento no lado oposto.
 * A sequência de pontuação é GPP -> PGP -> PPG.
 */
@Autonomous(name = "AutoBlueSouth", group = "Autonomous")
@Disabled
public class AutoBlueSouth extends LinearOpMode {

    // --- POSIÇÕES BASE (Baseadas em MeepMeepSouth.java) ---
    final double startPositionX = 62;
    final double startPositionY = -12;

    final double launchPositionX = 50;
    final double launchPositionY = -12;

    final double ppgPositionX = -12;
    final double ppgPositionY = -56;

    final double pgpPositionX = 15;
    final double pgpPositionY = -63;

    final double gppPositionX = 36;
    final double gppPositionY = -63;

    final double preY = -25;

    final double startPositionHeading = Math.toRadians(0);
    final double launchPositionHeading = Math.toRadians(20);
    final double modifPositionHeading = Math.toRadians(270);

    final Vector2d startVector = new Vector2d(startPositionX, startPositionY);
    final Vector2d launchVector = new Vector2d(launchPositionX, launchPositionY);
    final Vector2d ppgVector = new Vector2d(ppgPositionX, ppgPositionY);
    final Vector2d pgpVector = new Vector2d(pgpPositionX, pgpPositionY);
    final Vector2d gppVector = new Vector2d(gppPositionX, gppPositionY);

    final Vector2d prePpgVector = new Vector2d(ppgPositionX, preY);
    final Vector2d prePgpVector = new Vector2d(pgpPositionX, preY);
    final Vector2d preGppVector = new Vector2d(gppPositionX, preY);

    final Pose2d startPose = new Pose2d(startVector, startPositionHeading);
    final Pose2d launchPose = new Pose2d(launchVector, launchPositionHeading);
    final Pose2d ppgPose = new Pose2d(ppgVector, modifPositionHeading);
    final Pose2d pgpPose = new Pose2d(pgpVector, modifPositionHeading);
    final Pose2d gppPose = new Pose2d(gppVector, modifPositionHeading);
    String zone = "south";
    DelayAction launchDelay = new DelayAction(1.5);
    final double indexPower = 0.392;
    final double indexSeconds = 3.35;


    @Override
    public void runOpMode(){

        Intake intake = new Intake(hardwareMap, telemetry);
        LauncherPower launcher = new LauncherPower(hardwareMap, telemetry);
        MecanumDrive drive = new MecanumDrive(hardwareMap, startPose);
        Voltage voltage = new Voltage(hardwareMap, telemetry);


        // --- TRAJETÓRIAS ---

        // 1. Início para o 1º Ponto de Lançamento (Launch)
        TrajectoryActionBuilder goToLaunch = drive.actionBuilder(startPose)
                .strafeToLinearHeading(launchVector, launchPositionHeading);

        TrajectoryActionBuilder goToGPP = drive.actionBuilder(launchPose)
                .strafeToLinearHeading(preGppVector, modifPositionHeading)
                .strafeTo(gppVector)
                ;

        TrajectoryActionBuilder returnToLaunchByGPP =drive.actionBuilder(gppPose)
                .strafeToLinearHeading(launchVector, launchPositionHeading)
                ;

        TrajectoryActionBuilder goToPGP = drive.actionBuilder(launchPose)
                .strafeToLinearHeading(prePgpVector, modifPositionHeading)
                .strafeTo(pgpVector)
                ;

        TrajectoryActionBuilder returnToLaunchByPGP = drive.actionBuilder(pgpPose)
                .strafeToLinearHeading(launchVector, launchPositionHeading)
                ;

        TrajectoryActionBuilder goToPPG = drive.actionBuilder(launchPose)
                .strafeToLinearHeading(prePpgVector, modifPositionHeading)
                .strafeTo(ppgVector)
                ;

        TrajectoryActionBuilder returnToLaunchByPPG = drive.actionBuilder(ppgPose)
                .strafeToLinearHeading(launchVector, launchPositionHeading)
                ;


        // ======================================
        // SELEÇÃO DE POTÊNCIA (antes do start)
        // ======================================

        double flywheelPower =voltage.getMaxPowerVoltage(zone);
        voltage.sendTelemetry();
        telemetry.update();

        waitForStart();

        // --- SEQUÊNCIA DE AÇÕES ---
        Actions.runBlocking(
                new ParallelAction(
                        // Ações paralelas
                        intake.onAuto(),
                        launcher.onAuto(flywheelPower),
                        // Ações sequenciais: Movimentação e Lançamento
                        new SequentialAction(
                                launchDelay,
                                // 1. Lançamento GPP
                                goToLaunch.build(), // Move para a 1ª posição de lançamento
                                launcher.launch(indexPower, indexSeconds),  // Lança 1º elemento
                                goToGPP.build(),    // Vai para GPP
                                returnToLaunchByGPP.build(), // Volta para Launch
                                // 2. Lançamento PGP
                                launcher.launch(indexPower, indexSeconds),  // Lança 2º elemento
                                goToPGP.build(),    // Vai para PGP
                                returnToLaunchByPGP.build(), // Move para a posição de lançamento
                                // 3. Lançamento PPG
                                launcher.launch(indexPower, indexSeconds),  // Lança 3º elemento
                                goToPPG.build(),    // Vai para PPG
                                returnToLaunchByPPG.build(), // Volta para Launch
                                // 4. Lançamento final (se houver 4ª peça)
                                launcher.launch(indexPower, indexSeconds) // Lança 4º elemento (parado Launch)
                        )
                )
        );
    }
}