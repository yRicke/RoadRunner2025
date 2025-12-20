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
import org.firstinspires.ftc.teamcode.subsystems.LauncherVelocity;

@Autonomous(name = "AutoRedSouthVelocity", group = "Autonomous")
public class AutoRedSouthVelocity extends LinearOpMode {

    // --- POSIÇÕES BASE ---
    final double startPositionX = 62;
    final double startPositionY = 12;

    final double launchPositionX = 55;
    final double launchPositionY = 12;

    final double ppgPositionX = -12;
    final double ppgPositionY = 57;

    final double pgpPositionX = 15;
    final double pgpPositionY = 64;

    final double gppPositionX = 36;
    final double gppPositionY = 64;

    final double preY = 25;

    final double startPositionHeading = Math.toRadians(0);
    final double launchPositionHeading = Math.toRadians(-24);
    final double modifPositionHeading = Math.toRadians(-270);

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

    @Override
    public void runOpMode(){

        Intake intake = new Intake(hardwareMap, telemetry);
        LauncherVelocity launcher = new LauncherVelocity(hardwareMap, telemetry);
        MecanumDrive drive = new MecanumDrive(hardwareMap, startPose);
        launcher.setAutoMode(true);


        //Trajetórias
        TrajectoryActionBuilder goToLaunch = drive.actionBuilder(startPose)
                .strafeToLinearHeading(launchVector, launchPositionHeading);

        TrajectoryActionBuilder goToGPP = drive.actionBuilder(launchPose)
                .strafeToLinearHeading(preGppVector, modifPositionHeading)
                .strafeTo(gppVector)
                ;

        TrajectoryActionBuilder returnToLaunchByGPP = drive.actionBuilder(gppPose)
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
                .strafeToLinearHeading(prePpgVector, modifPositionHeading)
                .strafeToLinearHeading(launchVector, launchPositionHeading)
                ;

        waitForStart();


        Actions.runBlocking(
                new ParallelAction(
                        intake.onAuto(),
                        launcher.onAuto(false, true, false),  // (normal, longe, perto) normal mode rodando SEMPRE
                        new SequentialAction(
                                goToLaunch.build(),
                                launcher.launch(2.0),
                                goToGPP.build(),
                                returnToLaunchByGPP.build(),
                                launcher.launch(2.0),
                                goToPGP.build(),
                                returnToLaunchByPGP.build(),
                                launcher.launch(2.0),
                                goToPPG.build(),
                                returnToLaunchByPPG.build(),
                                launcher.launch(2.0),
                                goToPGP.build()
                        )
                )
        );
    }
}