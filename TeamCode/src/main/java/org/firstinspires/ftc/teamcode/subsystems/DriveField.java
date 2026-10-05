package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

/** Drive relativo ao campo, reutilizando os motores e controles do Drive. */
public class DriveField extends Drive {

    private final IMU imu;
    private final Telemetry telemetry;

    public DriveField(HardwareMap hardwareMap, Telemetry telemetry) {
        super(hardwareMap, telemetry);
        this.telemetry = telemetry;

        imu = hardwareMap.get(IMU.class, "imu");
        // Mesma montagem configurada em MecanumDrive: logo à direita, USB à frente.
        RevHubOrientationOnRobot orientation = new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.RIGHT,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD);
        if (!imu.initialize(new IMU.Parameters(orientation))) {
            throw new IllegalStateException("Não foi possível inicializar a IMU do DriveField.");
        }
    }

    /** Axial e lateral são relativos ao campo; yaw continua relativo ao robô. */
    @Override
    public void run(double axial, double lateral, double yaw,
                    boolean precisionMode, double correctionYaw) {
        // Corrige os sinais de translação recebidos dos teleops.
        axial = -axial;
        lateral = -lateral;

        double heading = getHeadingRadians();
        double cos = Math.cos(heading);
        double sin = Math.sin(heading);

        // Compensa o heading antes de aplicar as equações mecanum do Drive.
        double robotAxial = axial * cos - lateral * sin;
        double robotLateral = lateral * cos + axial * sin;

        super.run(robotAxial, robotLateral, yaw, precisionMode, correctionYaw);
    }

    /** A direção atual do robô passa a ser a referência zero do campo. */
    public void resetYaw() {
        imu.resetYaw();
    }

    private double getHeadingRadians() {
        return imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
    }

    @Override
    public void sendTelemetry() {
        super.sendTelemetry();
        telemetry.addData("Field Heading (deg)", Math.toDegrees(getHeadingRadians()));
    }
}
