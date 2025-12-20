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

@Autonomous(name = "AutoRedNorthMeczadaDosCria", group = "Autonomous")
public class AutoRedNorthMeczadaDosCria extends LinearOpMode {

    // --- POSIÇÕES BASE ---
    final double startPositionX = -62;
    final double startPositionY = 12;

    final double launchPositionX = -15;
    final double launchPositionY = 15;

    final double ppgPositionX = -12;
    final double ppgPositionY = 54;

    final double pgpPositionX = 14;

    final double pgpPositionY = 64;

    final double gppPositionX = 36;
    final double gppPositionY = 63;

    final double preY = 25;

    final double gatePositionX = -4;
    final double gatePositionY = 55.5;
    final double pgpreturnPositionX = 20;
    final double pgpreturnPositionY = 55;

    final double startPositionHeading = Math.toRadians(-90);
    final double launchPositionHeading = Math.toRadians(-45);
    final double modifPositionHeading = Math.toRadians(-270);
    final double returnPositionHeading = Math.toRadians(-278);

    final Vector2d startVector = new Vector2d(startPositionX, startPositionY);
    final Vector2d launchVector = new Vector2d(launchPositionX, launchPositionY);
    final Vector2d ppgVector = new Vector2d(ppgPositionX, ppgPositionY);
    final Vector2d pgpVector = new Vector2d(pgpPositionX, pgpPositionY);
    final Vector2d gppVector = new Vector2d(gppPositionX, gppPositionY);
    final Vector2d gateVector = new Vector2d(gatePositionX, gatePositionY);
    final Vector2d pgpReturnVector = new Vector2d(pgpreturnPositionX, pgpreturnPositionY);

    final Vector2d prePpgVector = new Vector2d(ppgPositionX, preY);
    final Vector2d prePgpVector = new Vector2d(pgpPositionX, preY);
    final Vector2d preGppVector = new Vector2d(gppPositionX, preY);
    final Vector2d preGateVector = new Vector2d(gatePositionX, gatePositionY-12);

    final Pose2d startPose = new Pose2d(startVector, startPositionHeading);
    final Pose2d launchPose = new Pose2d(launchVector, launchPositionHeading);
    final Pose2d ppgPose = new Pose2d(ppgVector, modifPositionHeading);
    final Pose2d pgpPose = new Pose2d(pgpVector, modifPositionHeading);
    final Pose2d gppPose = new Pose2d(gppVector, modifPositionHeading);
    final Pose2d gatePose = new Pose2d(gateVector, modifPositionHeading);
    @Override
    public void runOpMode(){

        Intake intake = new Intake(hardwareMap, telemetry);
        LauncherVelocity launcher = new LauncherVelocity(hardwareMap, telemetry);
        MecanumDrive drive = new MecanumDrive(hardwareMap, startPose);
        launcher.setAutoMode(true);


        //Trajetórias
        TrajectoryActionBuilder goToLaunch = drive.actionBuilder(startPose)
                .strafeToLinearHeading(launchVector, launchPositionHeading);

        TrajectoryActionBuilder goToPPG = drive.actionBuilder(launchPose)
                .strafeToLinearHeading(prePpgVector, modifPositionHeading)
                .strafeToConstantHeading(ppgVector)
                ;
        TrajectoryActionBuilder goToGate = drive.actionBuilder(ppgPose)
                .strafeToConstantHeading(preGateVector)
                .strafeToConstantHeading(gateVector)
                ;

        TrajectoryActionBuilder returnToLaunchByPPG = drive.actionBuilder(gatePose)
                .strafeToLinearHeading(launchVector, launchPositionHeading)
                ;

        TrajectoryActionBuilder goToPGP = drive.actionBuilder(launchPose)
                .strafeToLinearHeading(prePgpVector, modifPositionHeading)
                .strafeToConstantHeading(pgpVector)
                ;

        TrajectoryActionBuilder returnToLaunchByPGP = drive.actionBuilder(pgpPose)
                .strafeToLinearHeading(pgpReturnVector, returnPositionHeading)
                .strafeToLinearHeading(launchVector, launchPositionHeading)
                ;

        TrajectoryActionBuilder goToGPP = drive.actionBuilder(launchPose)
                .strafeToSplineHeading(preGppVector, modifPositionHeading)
                .strafeToConstantHeading(gppVector)
                ;

        TrajectoryActionBuilder returnToLaunchByGPP = drive.actionBuilder(gppPose)
                .strafeToLinearHeading(launchVector, launchPositionHeading)
                ;

        waitForStart();


        Actions.runBlocking(
                new ParallelAction(
                        intake.onAuto(),
                        launcher.onAuto(true, false, false),  // (normal, longe, perto) normal mode rodando SEMPRE
                        new SequentialAction(
                                goToLaunch.build(),
                                launcher.launch(2.0),
                                goToPPG.build(),
                                goToGate.build(),
                                returnToLaunchByPPG.build(),
                                launcher.launch(1.8),
                                goToPGP.build(),
                                returnToLaunchByPGP.build(),
                                launcher.launch(1.8),
                                goToGPP.build(),
                                returnToLaunchByGPP.build(),
                                launcher.launch(1.8),
                                goToGPP.build()
                        )
                )
        );
    }
}