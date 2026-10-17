package org.firstinspires.ftc.teamcode.subsystems;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Trigger {

    private Servo trigger = null;

    private Telemetry telemetry;

    public final double TRIGGER_OPEN_POSITION = 1.0; // Posição aberta do gatilho
    public final double TRIGGER_CLOSED_POSITION = 0.5; // Posição fechada do gatilho

    // Construtor recebe o hardwareMap e telemetry do OpMode
    public Trigger(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;
        trigger = hardwareMap.get(Servo.class, "trigger");
    }

    // Executa o intake com base no gatilho
    public void run(boolean trigger_on) {
        if (trigger_on) {
            setTriggerOpen();
        }
        else{
            setTriggerClosed();
        }
    }

    public void setTriggerOpen(){
        trigger.setPosition(TRIGGER_OPEN_POSITION); // Abre o gatilho
    }
    public void setTriggerClosed(){
        trigger.setPosition(TRIGGER_CLOSED_POSITION); // Fecha o gatilho
    }

    // Telemetria
    public void sendTelemetry() {
        double servoPosition = trigger.getPosition();
        telemetry.addData("Trigger State", servoPosition == TRIGGER_CLOSED_POSITION ? "Closed" : "Open");
        telemetry.addData("Trigger Position", servoPosition);
    }

    public Action triggerOpen() {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    setTriggerOpen();
                    initialized = true;
                }
                return false;
            }
        };
    }
    public Action triggerClosed() {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    setTriggerClosed();
                    initialized = true;
                }
                return false;
            }
        };
    }
}
