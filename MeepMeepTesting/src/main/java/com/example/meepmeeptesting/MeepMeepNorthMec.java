package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.core.colorscheme.scheme.ColorSchemeBlueDark;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class MeepMeepNorthMec {

    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(500);

        // ==========================
        //   BOT VERMELHO (lado y positivo)
        // ==========================
        RoadRunnerBotEntity redBot = new DefaultBotBuilder(meepMeep)
                .setColorScheme(new ColorSchemeBlueDark())
                .setConstraints(100, 100, Math.toRadians(180), Math.toRadians(180), 15)
                .setDimensions(17.783, 18)
                .build();

        // --- POSIÇÕES BASE ---
        final double startPositionX = -62;
        final double startPositionY = 12;

        final double launchPositionX = -12;
        final double launchPositionY = 12;

        final double ppgPositionX = -12;
        final double ppgPositionY = 55;

        final double pgpPositionX = 15;
        final double pgpPositionY = 63;

        final double gppPositionX = 36;
        final double gppPositionY = 63;

        final double preY = 25;

        final double gatePositionX = -1;
        final double gatePositionY = 54;

        final double pgpreturnPositionX = 23;
        final double pgpreturnPositionY = 50;

        final double startPositionHeading = Math.toRadians(-90);
        final double launchPositionHeading = Math.toRadians(-45);
        final double modifPositionHeading = Math.toRadians(90);

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


        TrajectoryActionBuilder conjunto = redBot.getDrive().actionBuilder(startPose)
                //Launch
                .strafeToLinearHeading(launchVector, launchPositionHeading)

                .strafeToLinearHeading(prePpgVector, modifPositionHeading)
                .strafeToConstantHeading(ppgVector)

                .strafeToConstantHeading(preGateVector)
                .strafeToConstantHeading(gateVector)


                .strafeToLinearHeading(launchVector, launchPositionHeading)


                .strafeToLinearHeading(prePgpVector, modifPositionHeading)
                .strafeToConstantHeading(pgpVector)


                .strafeToLinearHeading(pgpReturnVector, modifPositionHeading - Math.toRadians(-10))
                .strafeToLinearHeading(launchVector, launchPositionHeading)

                .strafeToLinearHeading(preGppVector, modifPositionHeading)
                .strafeToConstantHeading(gppVector)


                .strafeToLinearHeading(launchVector, launchPositionHeading)                ;


        redBot.runAction(
                conjunto.build()
        );

        // ==========================
        //   VISUALIZAÇÃO
        // ==========================
        meepMeep.setBackground(MeepMeep.Background.FIELD_DECODE_JUICE_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(redBot)
                .start();
    }
}