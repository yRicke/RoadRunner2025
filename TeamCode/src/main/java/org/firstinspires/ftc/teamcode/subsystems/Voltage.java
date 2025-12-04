package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.firstinspires.ftc.robotcore.external.Telemetry;

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
    }

    public double getMaxPowerVoltage(String zone) {
        double power;
        if (zone == "south" ) {
            power = - 0.016666 * initVoltage + 0.88833;
        }
        else if (zone == "north" ){
            power = -0.25 * initVoltage + 4.3;
        } else {
            power = 1.0;
        }
        return power;
    }
}
