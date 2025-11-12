package org.firstinspires.ftc.teamcode.TeleOp.Tests;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.teamcode.subsystems.AprilTagWebcam;

@Autonomous(name = "Test - AprilTag ID20", group = "Tests")
public class ID20AprilTagWebcamTest extends OpMode {

    private AprilTagWebcam aprilTag;

    @Override
    public void init() {
        telemetry.addLine("Inicializando câmera...");
        telemetry.update();

        // Inicializa o subsistema (novo padrão)
        aprilTag = new AprilTagWebcam(hardwareMap, telemetry);

        telemetry.addLine("Câmera pronta! Aguarde início...");
        telemetry.update();
    }

    @Override
    public void loop() {
        // Atualiza as leituras da câmera
        aprilTag.run();

        // Busca a tag de ID 20
        AprilTagDetection tag20 = aprilTag.getTagByID(20);

        // Mostra informações da tag (distância e ângulo)
        aprilTag.sendTelemetry(tag20);
        telemetry.update();
    }

    @Override
    public void stop() {
        // Fecha o VisionPortal de forma segura
        aprilTag.stop();
    }
}
