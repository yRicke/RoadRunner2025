package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.Objects;

public class Voltage {

    VoltageSensor myControlHubVoltageSensor;
    private Telemetry telemetry;
    double initVoltage;

    public Voltage(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;
        myControlHubVoltageSensor = hardwareMap.get(VoltageSensor.class, "Control Hub");
        initVoltage = myControlHubVoltageSensor.getVoltage();

    }
    public void sendTelemetry() {
        telemetry.addData("Battery Voltage", "%.2f volts", myControlHubVoltageSensor.getVoltage());
        telemetry.addData("North Power", "%.3f", getMaxPowerVoltage("north"));
        telemetry.addData("South Power", "%.3f", getMaxPowerVoltage("south"));
    }

    public double getMaxPowerVoltage(String zone) {
        double power;
        if (Objects.equals(zone, "north")) {
            power = Range.clip(- 0.013222 * initVoltage + 0.85833, 0.6, 1);
        }
        else if (Objects.equals(zone, "south")){
            power = Range.clip(- 0.0114 * initVoltage + 0.973124, 0.75, 1);
        } else {
            power = 1.0;
        }
        return power;
    }
}
