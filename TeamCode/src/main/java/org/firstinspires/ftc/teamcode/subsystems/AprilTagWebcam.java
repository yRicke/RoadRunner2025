package org.firstinspires.ftc.teamcode.subsystems;

import android.util.Size;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.ArrayList;
import java.util.List;

public class AprilTagWebcam {
    private double lastDistance = 0; // guarda a última distância válida
    private final AprilTagProcessor aprilTagProcessor;
    private final VisionPortal visionPortal;
    private final Telemetry telemetry;

    private List<AprilTagDetection> detectedTags = new ArrayList<>();

    // Construtor padrão como outros subsistemas
    public AprilTagWebcam(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;

        aprilTagProcessor = new AprilTagProcessor.Builder()
                .setDrawTagID(false)
                .setDrawTagOutline(true)
                .setDrawAxes(false)
                .setDrawCubeProjection(false)
                .setOutputUnits(DistanceUnit.CM, AngleUnit.DEGREES)
                .build();

        VisionPortal.Builder builder = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "webcam_daora"))
                .setCameraResolution(new Size(640, 480
                )) // mais leve e estável
                .addProcessor(aprilTagProcessor);

        visionPortal = builder.build();
    }

    /** Atualiza a leitura das tags (chamar a cada loop) */
    public void run() {
        if (aprilTagProcessor == null) return;
        List<AprilTagDetection> detections = aprilTagProcessor.getDetections();
        if (detections != null) {
            detectedTags = detections;
        }
    }

    /** Retorna a primeira tag detectada com ID específico */
    public AprilTagDetection getTagByID(int id) {
        for (AprilTagDetection tag : detectedTags) {
            if (tag.id == id) {
                return tag;
            }
        }
        return null;
    }

    /** Retorna a distância em cm de uma detecção (seguro contra null) */
    public double getRange(AprilTagDetection tag) {
        if (tag != null && tag.ftcPose != null && !Double.isNaN(tag.ftcPose.y)) {
            lastDistance = tag.ftcPose.y; // atualiza a última distância válida
        }
        return lastDistance; // retorna a última distância se perder a detecção
    }


    /** Mostra informações da tag no telemetry */
    public void sendTelemetry(AprilTagDetection tag) {
        if (tag == null) {
            telemetry.addLine("Nenhuma AprilTag detectada");
            return;
        }

        if (tag.ftcPose != null) {
            telemetry.addLine(String.format("ID %d detectada", tag.id));
            telemetry.addData("Distância (cm)", "%.1f", tag.ftcPose.y);
            telemetry.addData("Ângulo (deg)", "%.1f", tag.ftcPose.yaw);
        } else {
            telemetry.addLine(String.format("ID %d detectada, mas sem pose", tag.id));
        }
    }

    /** Fecha a câmera de forma segura */
    public void stop() {
        if (visionPortal != null) {
            visionPortal.close();
        }
    }
}
