package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.core.colorscheme.scheme.ColorSchemeBlueDark;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class MeepMeepSouth {

    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(400);

        // ==========================
        //   BOT AZUL (lado y negativo)
        // ==========================
        RoadRunnerBotEntity blueBot = new DefaultBotBuilder(meepMeep)
                .setColorScheme(new ColorSchemeBlueDark())
                .setConstraints(100, 60, Math.toRadians(180), Math.toRadians(180), 15)
                .setDimensions(17.783, 17.617)
                .build();

        // --- POSIÇÕES BASE ---
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
        final double launchPositionHeading = Math.toRadians(30);
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

        // Trajetórias
        TrajectoryActionBuilder goToLaunch = blueBot.getDrive().actionBuilder(startPose)
                .strafeToLinearHeading(launchVector, launchPositionHeading);

        TrajectoryActionBuilder goToGPP = blueBot.getDrive().actionBuilder(launchPose)
                .strafeToLinearHeading(preGppVector, modifPositionHeading)
                .strafeTo(gppVector)
                ;

        TrajectoryActionBuilder returnToLaunchByGPP = blueBot.getDrive().actionBuilder(gppPose)
                .strafeToLinearHeading(launchVector, launchPositionHeading)
                ;

        TrajectoryActionBuilder goToPGP = blueBot.getDrive().actionBuilder(launchPose)
                .strafeToLinearHeading(prePgpVector, modifPositionHeading)
                .strafeTo(pgpVector)
                ;

        TrajectoryActionBuilder goToLaunchByPGP = blueBot.getDrive().actionBuilder(pgpPose)
                .strafeToLinearHeading(launchVector, launchPositionHeading)
                ;

        TrajectoryActionBuilder goToPPG = blueBot.getDrive().actionBuilder(launchPose)
                .strafeToLinearHeading(prePpgVector, modifPositionHeading)
                .strafeTo(ppgVector)
                ;

        TrajectoryActionBuilder goToSecondLaunchByPPG = blueBot.getDrive().actionBuilder(ppgPose)
                .strafeToLinearHeading(launchVector, launchPositionHeading)
                ;

        TrajectoryActionBuilder conjunto = blueBot.getDrive().actionBuilder(startPose)
                // Ir para Launch
                .strafeToLinearHeading(launchVector, launchPositionHeading)
                // Ir para GPP
                .strafeToLinearHeading(preGppVector, modifPositionHeading)
                .strafeTo(gppVector)
                // Voltar para Launch
                .strafeToLinearHeading(launchVector, launchPositionHeading)
                // Ir para PGP
                .strafeToLinearHeading(prePgpVector, modifPositionHeading)
                .strafeTo(pgpVector)
                // Ir para Launch
                .strafeToLinearHeading(launchVector, launchPositionHeading)
                // Ir para PPG
                .strafeToLinearHeading(prePpgVector, modifPositionHeading)
                .strafeTo(ppgVector)
                // Voltar para Launch
                .strafeToLinearHeading(launchVector, launchPositionHeading)




                ;

        blueBot.runAction(
                conjunto.build()
        );

        // ==========================
        //   VISUALIZAÇÃO
        // ==========================
        meepMeep.setBackground(MeepMeep.Background.FIELD_DECODE_JUICE_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(blueBot)
                .start();
    }
}