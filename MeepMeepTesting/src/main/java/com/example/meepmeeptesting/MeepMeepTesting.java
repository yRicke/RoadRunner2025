package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.core.colorscheme.scheme.ColorSchemeBlueDark;
import com.noahbres.meepmeep.core.colorscheme.scheme.ColorSchemeRedDark;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class MeepMeepTesting {

    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(400);

        // ==========================
        //   BOT AZUL (lado y negativo)
        // ==========================
        RoadRunnerBotEntity blueBot = new DefaultBotBuilder(meepMeep)
                .setColorScheme(new ColorSchemeBlueDark())
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 15)
                .setDimensions(18, 18)
                .build();

        // --- POSIÇÕES BASE (AZUL) ---
        final double startPositionX = -60;
        final double startPositionY = -13;

        final double launchPositionX = -13;
        final double launchPositionY = -13;

        final double ppgPositionX = -12;
        final double ppgPositionY = -56;

        final double pgpPositionX = 14;
        final double pgpPositionY = -65;

        final double gppPositionX = 36;
        final double gppPositionY = -65;

        final double preY = -25;

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

        final Pose2d startPose = new Pose2d(startVector, launchPositionHeading);
        final Pose2d launchPose = new Pose2d(launchVector, launchPositionHeading);
        final Pose2d ppgPose = new Pose2d(ppgVector, modifPositionHeading);
        final Pose2d pgpPose = new Pose2d(pgpVector, modifPositionHeading);
        final Pose2d gppPose = new Pose2d(gppVector, modifPositionHeading);


        blueBot.runAction(blueBot.getDrive().actionBuilder(startPose)

                .strafeTo(launchVector)
                //PPG
                .strafeToLinearHeading(prePpgVector, modifPositionHeading)
                .strafeTo(ppgVector)

                .strafeToLinearHeading(launchVector, launchPositionHeading)

                //PGP
                .strafeToLinearHeading(prePgpVector, modifPositionHeading)
                .strafeTo(pgpVector)

                .strafeToLinearHeading(launchVector, launchPositionHeading)

                //GPP
                .strafeToLinearHeading(preGppVector, modifPositionHeading)
                .strafeTo(gppVector)

                .strafeToLinearHeading(launchVector, launchPositionHeading)

                .build());

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