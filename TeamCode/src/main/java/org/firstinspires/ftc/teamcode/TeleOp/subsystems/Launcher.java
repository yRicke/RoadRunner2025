package org.firstinspires.ftc.teamcode.TeleOp.subsystems;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Launcher {

    private DcMotor launcherMotorOne = null;
    private DcMotor launcherMotorTwo = null;
    private CRServo indexServoOne = null;
    private CRServo indexServoTwo = null;



    private final double launcherMaxPower = 0.75;

    private Telemetry telemetry;

    // Construtor recebe o hardwareMap e telemetry do OpMode
    public Launcher(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;

        launcherMotorOne = hardwareMap.get(DcMotor.class, "launcher_motor_one");
        launcherMotorTwo = hardwareMap.get(DcMotor.class, "launcher_motor_two");
        indexServoOne = hardwareMap.get(CRServo.class, "index_servo_one");
        indexServoTwo = hardwareMap.get(CRServo.class, "index_servo_two");
        indexServoOne.setDirection(DcMotorSimple.Direction.REVERSE);

    }

    // Executa o launcher
    public void run(boolean on, boolean shoot) {
        if (on) {
            onMotors();
        } else {
            offMotors();
        }

        if (shoot) {
            onIndexServo();
        } else {
            offIndexServo();
        }
    }

    public void onIndexServo() {
        indexServoOne.setPower(1);
        indexServoTwo.setPower(1);
    }

    public void offIndexServo() {
        indexServoOne.setPower(0);
        indexServoTwo.setPower(0);
    }

    public void onMotors() {
        launcherMotorOne.setPower(-launcherMaxPower);
        launcherMotorTwo.setPower(launcherMaxPower);
    }

    public void offMotors() {
        launcherMotorOne.setPower(0);
        launcherMotorTwo.setPower(0);
    }

    public void sendTelemetry() {
        telemetry.addData("Launcher Motor One Power", launcherMotorOne.getPower());
        telemetry.addData("Launcher Motor Two Power", launcherMotorTwo.getPower());
        telemetry.addData("Index Servo Power", indexServoOne.getPower());
    }
}
