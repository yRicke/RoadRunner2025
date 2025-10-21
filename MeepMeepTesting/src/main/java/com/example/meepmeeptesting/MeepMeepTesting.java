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
        MeepMeep meepMeep = new MeepMeep(800);

        // ==========================
        //   BOT AZUL (lado y negativo)
        // ==========================
        RoadRunnerBotEntity blueBot = new DefaultBotBuilder(meepMeep)
                .setColorScheme(new ColorSchemeBlueDark())
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 15)
                .build();

        // --- POSIÇÕES BASE (AZUL) ---
        final double startPositionX = -25;
        final double startPositionY = -25;

        final double ppgPositionX = -12;
        final double ppgPositionY = -46;

        final double pgpPositionX = 12;
        final double pgpPositionY = -46;

        final double gppPositionX = 36;
        final double gppPositionY = -46;

        final double startHeading = Math.toRadians(225);
        final double modifHeading = Math.toRadians(270);

        Pose2d startPoseBlue = new Pose2d(startPositionX, startPositionY, startHeading);

        Vector2d startVectorBlue = new Vector2d(startPositionX, startPositionY);
        Vector2d ppgVectorBlue = new Vector2d(ppgPositionX, ppgPositionY);
        Vector2d pgpVectorBlue = new Vector2d(pgpPositionX, pgpPositionY);
        Vector2d gppVectorBlue = new Vector2d(gppPositionX, gppPositionY);

        Vector2d prePpgVectorBlue = new Vector2d(ppgPositionX, -34);
        Vector2d prePgpVectorBlue = new Vector2d(pgpPositionX, -34);
        Vector2d preGppVectorBlue = new Vector2d(gppPositionX, -34);

        blueBot.runAction(blueBot.getDrive().actionBuilder(startPoseBlue)
                // --- PPG ---
                .strafeToLinearHeading(prePpgVectorBlue, modifHeading)
                .strafeTo(ppgVectorBlue)
                .strafeToLinearHeading(startVectorBlue, startHeading)

                // --- PGP ---
                .strafeToLinearHeading(prePgpVectorBlue, modifHeading)
                .strafeTo(pgpVectorBlue)
                .strafeToLinearHeading(startVectorBlue, startHeading)

                // --- GPP ---
                .strafeToLinearHeading(preGppVectorBlue, modifHeading)
                .strafeTo(gppVectorBlue)
                .strafeToLinearHeading(startVectorBlue, startHeading)
                .build());


        // ==========================
        //   BOT VERMELHO (lado y positivo)
        // ==========================
        RoadRunnerBotEntity redBot = new DefaultBotBuilder(meepMeep)
                .setColorScheme(new ColorSchemeRedDark())
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 15)
                .build();


        Pose2d startPoseRed = new Pose2d(startPositionX, -startPositionY, -startHeading);

        Vector2d startVectorRed = new Vector2d(startPositionX, -startPositionY);
        Vector2d ppgVectorRed = new Vector2d(ppgPositionX, -ppgPositionY);
        Vector2d pgpVectorRed = new Vector2d(pgpPositionX, -pgpPositionY);
        Vector2d gppVectorRed = new Vector2d(gppPositionX, -gppPositionY);

        Vector2d prePpgVectorRed = new Vector2d(ppgPositionX, 34);
        Vector2d prePgpVectorRed = new Vector2d(pgpPositionX, 34);
        Vector2d preGppVectorRed = new Vector2d(gppPositionX, 34);

        redBot.runAction(redBot.getDrive().actionBuilder(startPoseRed)
                // --- PPG ---
                .strafeToLinearHeading(prePpgVectorRed, -modifHeading)
                .strafeTo(ppgVectorRed)
                .strafeToLinearHeading(startVectorRed, -modifHeading)

                // --- PGP ---
                .strafeToLinearHeading(prePgpVectorRed, -modifHeading)
                .strafeTo(pgpVectorRed)
                .strafeToLinearHeading(startVectorRed, -modifHeading)

                // --- GPP ---
                .strafeToLinearHeading(preGppVectorRed, -modifHeading)
                .strafeTo(gppVectorRed)
                .strafeToLinearHeading(startVectorRed, -modifHeading)
                .build());


        // ==========================
        //   VISUALIZAÇÃO
        // ==========================
        meepMeep.setBackground(MeepMeep.Background.FIELD_DECODE_JUICE_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(blueBot)
                .addEntity(redBot)
                .start();
    }
}