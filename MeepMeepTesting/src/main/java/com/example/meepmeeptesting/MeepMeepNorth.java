package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.core.colorscheme.scheme.ColorSchemeBlueDark;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;
public class MeepMeepNorth {

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
        final double startPositionX = -62;
        final double startPositionY = -12;

        final double launchPositionX = -12;
        final double launchPositionY = -12;

        final double ppgPositionX = -12;
        final double ppgPositionY = -52;

        final double pgpPositionX = 15;
        final double pgpPositionY = -59;

        final double gppPositionX = 36;
        final double gppPositionY = -55;

        final double preY = -25;

        final double startPositionHeading = Math.toRadians(90);
        final double launchPositionHeading = Math.toRadians(45);
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
                .strafeToLinearHeading(launchVector, launchPositionHeading)
                ;

        TrajectoryActionBuilder goToPPG = blueBot.getDrive().actionBuilder(launchPose)
                .splineToLinearHeading(new Pose2d(prePpgVector, modifPositionHeading), Math.toRadians(270))
                .splineToConstantHeading(ppgVector, Math.toRadians(270))
                ;

        TrajectoryActionBuilder returnToLaunchByPPG = blueBot.getDrive().actionBuilder(ppgPose)
                .splineToLinearHeading(new Pose2d(launchVector, launchPositionHeading), Math.toRadians(90))
                ;

        TrajectoryActionBuilder goToPGP = blueBot.getDrive().actionBuilder(launchPose)
                .splineToLinearHeading(new Pose2d(prePgpVector, modifPositionHeading), Math.toRadians(270))
                .splineToConstantHeading(pgpVector, Math.toRadians(270))
                ;

        TrajectoryActionBuilder returnToLaunchByPGP = blueBot.getDrive().actionBuilder(pgpPose)
                .splineToLinearHeading(new Pose2d(launchVector, launchPositionHeading), Math.toRadians(-180))
                ;

        TrajectoryActionBuilder goToGPP = blueBot.getDrive().actionBuilder(launchPose)
                .splineToLinearHeading(new Pose2d(preGppVector, modifPositionHeading), Math.toRadians(270))
                .splineToConstantHeading(gppVector, Math.toRadians(270))
                ;

        TrajectoryActionBuilder returnToLaunchByGPP = blueBot.getDrive().actionBuilder(gppPose)
                .splineToLinearHeading(new Pose2d(launchVector, launchPositionHeading), Math.toRadians(-180))
                ;

        TrajectoryActionBuilder conjunto = blueBot.getDrive().actionBuilder(startPose)
                // Launch
                .strafeToLinearHeading(launchVector, launchPositionHeading)

                // PPG
                .splineToLinearHeading(new Pose2d(prePpgVector, modifPositionHeading), Math.toRadians(270))
                .splineToConstantHeading(ppgVector, Math.toRadians(270))
                .splineToLinearHeading(new Pose2d(launchVector, launchPositionHeading), Math.toRadians(90))

                // PGP
                .splineToLinearHeading(new Pose2d(prePgpVector, modifPositionHeading), Math.toRadians(270))
                .splineToConstantHeading(pgpVector, Math.toRadians(270))
                .splineToLinearHeading(new Pose2d(launchVector, launchPositionHeading), Math.toRadians(-180))

                // GPP
                .splineToLinearHeading(new Pose2d(preGppVector, modifPositionHeading), Math.toRadians(270))
                .splineToConstantHeading(gppVector, Math.toRadians(270))
                .splineToLinearHeading(new Pose2d(launchVector, launchPositionHeading), Math.toRadians(-180));


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