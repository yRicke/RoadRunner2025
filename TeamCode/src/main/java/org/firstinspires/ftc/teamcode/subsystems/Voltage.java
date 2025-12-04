package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Voltage {

    VoltageSensor myControlHubVoltageSensor = null;

    public Voltage(HardwareMap hardwareMap, Telemetry telemetry) {
        myControlHubVoltageSensor = hardwareMap.get(VoltageSensor.class, "Control Hub");
    }
    public void sendTelemetry(Telemetry telemetry) {
        telemetry.addData("Battery Voltage", "%.2f volts", myControlHubVoltageSensor.getVoltage());
    }
}
