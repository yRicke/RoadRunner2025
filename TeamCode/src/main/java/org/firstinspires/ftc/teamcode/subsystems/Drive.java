package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Drive {

    private DcMotor frontLeftDrive = null;
    private DcMotor backLeftDrive = null;
    private DcMotor frontRightDrive = null;
    private DcMotor backRightDrive = null;
    private double precisionControlMultiplier = 1;

    private Telemetry telemetry;

    // Construtor recebe o hardwareMap e telemetry do OpMode
    public Drive(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;

        // Inicializar os motores
        frontLeftDrive = hardwareMap.get(DcMotor.class, "front_left_drive");
        backLeftDrive = hardwareMap.get(DcMotor.class, "back_left_drive");
        frontRightDrive = hardwareMap.get(DcMotor.class, "front_right_drive");
        backRightDrive = hardwareMap.get(DcMotor.class, "back_right_drive");

        // Direções – verificar no robô se estão corretas
        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.FORWARD);
        backRightDrive.setDirection(DcMotor.Direction.FORWARD);

        //Encoderes
    }

    // Executa a movimentação do drive
    public void run(double axial, double lateral, double yaw, boolean precisionMode, double correctionYaw) {
        setPrecisionControlMultiplier(precisionMode);

        yaw += correctionYaw*0.3;

        double frontLeftPower  = (axial + lateral + yaw) * precisionControlMultiplier;
        double frontRightPower = (axial - lateral - yaw) * precisionControlMultiplier;
        double backLeftPower   = (axial - lateral + yaw) * precisionControlMultiplier;
        double backRightPower  = (axial + lateral - yaw) * precisionControlMultiplier;

        // Normalização para não passar de 100%
        double max = Math.max(Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower)),
                Math.max(Math.abs(backLeftPower), Math.abs(backRightPower)));

        if (max > 1.0) {
            frontLeftPower  /= max;
            frontRightPower /= max;
            backLeftPower   /= max;
            backRightPower  /= max;
        }

        // Define potência nos motores
        frontLeftDrive.setPower(frontLeftPower);
        frontRightDrive.setPower(frontRightPower);
        backLeftDrive.setPower(backLeftPower);
        backRightDrive.setPower(backRightPower);
    }

    // Telemetria
    public void sendTelemetry() {
        telemetry.addData("Front Left Power", frontLeftDrive.getPower());
        telemetry.addData("Front Right Power", frontRightDrive.getPower());
        telemetry.addData("Back Left Power", backLeftDrive.getPower());
        telemetry.addData("Back Right Power", backRightDrive.getPower());
    }

    // Controle de precisão
    private void setPrecisionControlMultiplier(boolean precisionMode) {
        precisionControlMultiplier = precisionMode ? 0.3 : 1.0;
    }
}
