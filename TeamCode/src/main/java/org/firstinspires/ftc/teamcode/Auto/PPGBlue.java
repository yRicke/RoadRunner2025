package org.firstinspires.ftc.teamcode.Auto;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.MecanumDrive;

@Autonomous(name = "PPGBlue", group = "Autonomous")
public class PPGBlue extends LinearOpMode {

    // ==========================
    //   POSIÇÕES DOS PONTOS
    // ==========================
    final  double startPositionX = -25;
    final double startPositionY = -25;

    final double ppgPositionX = -12;
    final double ppgPositionY = -46;

    final double pgpPositionX = 12;
    final double pgpPositionY = -46;

    final double gppPositionX = 36;
    final double gppPositionY = -46;

    final double startPostionHeading = Math.toRadians(225);
    final double modifPositionHeading = Math.toRadians(270);

    Pose2d startPose = new Pose2d(startPositionX,startPositionY, startPostionHeading);

    Vector2d startVector = new Vector2d(startPositionX, startPositionY);

    Vector2d ppgVector = new Vector2d(ppgPositionX, ppgPositionY);
    Vector2d pgpVector = new Vector2d(pgpPositionX, pgpPositionY);
    Vector2d gppVector = new Vector2d(gppPositionX, gppPositionY);

    // Pontos intermediários (x - 34)
    Vector2d prePpgVector= new Vector2d(ppgPositionX, -34);
    Vector2d prePgpVector= new Vector2d(pgpPositionX, -34);
    Vector2d preGppVector = new Vector2d(gppPositionX, -34);


    @Override
    public void runOpMode(){
        //Odometria
        MecanumDrive drive = new MecanumDrive(hardwareMap, startPose);
        //Trajetórias
        TrajectoryActionBuilder Geral = drive.actionBuilder(startPose)
                // --- PPG ---
                .strafeToLinearHeading(prePpgVector, modifPositionHeading)
                .strafeTo(ppgVector)
                // ponto final
                .strafeToLinearHeading(startVector, startPostionHeading)

                // --- PGP ---
                .strafeToLinearHeading(prePgpVector, modifPositionHeading)
                .strafeTo(pgpVector)
                // ponto final
                .strafeToLinearHeading(startVector, startPostionHeading)

                // --- GPP ---
                .strafeToLinearHeading(preGppVector, modifPositionHeading)
                .strafeTo(gppVector)
                // ponto final
                .strafeToLinearHeading(startVector, startPostionHeading)
                ;

        TrajectoryActionBuilder goToPPG = drive.actionBuilder(startPose)
                .strafeToLinearHeading(prePpgVector, modifPositionHeading)
                .strafeTo(ppgVector)
                ;

        TrajectoryActionBuilder goToPGP = drive.actionBuilder(startPose)
                .strafeToLinearHeading(prePgpVector, modifPositionHeading)
                .strafeTo(pgpVector)
                ;

        TrajectoryActionBuilder goToGPP = drive.actionBuilder(startPose)
                .strafeToLinearHeading(preGppVector, modifPositionHeading)
                .strafeTo(gppVector)
                ;

        TrajectoryActionBuilder returnToStartByPPG = drive.actionBuilder(new Pose2d(ppgVector, modifPositionHeading))
                .strafeToLinearHeading(startVector, startPostionHeading)
                ;

        TrajectoryActionBuilder returnToStartByPGP = drive.actionBuilder(new Pose2d(pgpVector, modifPositionHeading))
                .strafeToLinearHeading(startVector, startPostionHeading)
                ;

        TrajectoryActionBuilder returnToStartByGPP = drive.actionBuilder(new Pose2d(gppVector, modifPositionHeading))
                .strafeToLinearHeading(startVector, startPostionHeading)
                ;

        waitForStart();

        Actions.runBlocking(
                new SequentialAction(
                        goToPPG.build(),
                        returnToStartByPPG.build(),
                        goToPGP.build(),
                        returnToStartByPGP.build(),
                        goToGPP.build(),
                        returnToStartByGPP.build()
                )
        );
    }
}