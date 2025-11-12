package org.firstinspires.ftc.teamcode.Auto;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;

public class DelayAction implements Action {
    private final double delayTime;
    private double startTime = -1;

    public DelayAction(double delayTime) {
        this.delayTime = delayTime;
    }

    @Override
    public boolean run(@NonNull TelemetryPacket packet) {
        if (startTime < 0) {
            startTime = System.nanoTime() / 1e9;
        }
        double currentTime = System.nanoTime() / 1e9;
        return (currentTime - startTime) < delayTime;
    }
}

