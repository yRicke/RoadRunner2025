package com.example.meepmeeptesting;

import static java.sql.DriverManager.println;

import com.acmerobotics.roadrunner.geometry.Pose2d;

import org.rowlandhall.meepmeep.MeepMeep;
import org.rowlandhall.meepmeep.roadrunner.DefaultBotBuilder;
import org.rowlandhall.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class MeepMeepTesting {
    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(800);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 15)
                .followTrajectorySequence(drive -> drive.trajectorySequenceBuilder(new Pose2d(0, 0, 0))
                        .forward(30)
                        .turn(Math.toRadians(90))
                        .forward(30)
                        .turn(Math.toRadians(90))
                        .forward(30)
                        .turn(Math.toRadians(90))
                        .forward(30)
                        .turn(Math.toRadians(90))
                        .build());

        Image img = null;
        String user = System.getProperty("user.name");
        try { img = ImageIO.read(new File(("C:\\Users\\"+ user +"\\Documents\\Decode\\RoadRunner2025\\MeepMeepTesting\\src\\main\\java\\com\\example\\meepmeeptesting\\field\\DECODE.png"))); }
        catch(IOException e) {}

        meepMeep.setBackground(img)
                .setDarkMode(true)
                .setBackgroundAlpha(1f)
                .addEntity(myBot)
                .start();
    }
}