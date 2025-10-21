package org.firstinspires.ftc.teamcode.TeleOp.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Intake {

    private DcMotor intakeMotor = null;
    private double intakeMaxPower = 1.0;

    private Telemetry telemetry;

    // Construtor recebe o hardwareMap e telemetry do OpMode
    public Intake(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;
        intakeMotor = hardwareMap.get(DcMotor.class, "intake_motor");
        intakeMotor.setDirection(DcMotor.Direction.REVERSE);
    }

    // Executa o intake com base no gatilho
    public void run(boolean on, boolean eject) {
        if (on) {
            onMotor();
        }
        else if (eject){
            eject();
        }
        else{
            offMotor();
        }
    }

    public void onMotor(){
        intakeMotor.setPower(intakeMaxPower);
    }
    public void offMotor(){
        intakeMotor.setPower(0);
    }
    public void eject(){
        intakeMotor.setPower(-intakeMaxPower);
    }


    // Telemetria
    public void sendTelemetry() {
        telemetry.addData("Intake Power", intakeMotor.getPower());
    }
}
