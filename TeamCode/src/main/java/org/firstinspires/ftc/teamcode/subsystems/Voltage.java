package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Voltage {

    VoltageSensor myControlHubVoltageSensor;
    private Telemetry telemetry;

    public Voltage(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;
        myControlHubVoltageSensor = hardwareMap.get(VoltageSensor.class, "Control Hub");
    }
    public void sendTelemetry() {
        telemetry.addData("Battery Voltage", "%.2f volts", myControlHubVoltageSensor.getVoltage());
    }
}
