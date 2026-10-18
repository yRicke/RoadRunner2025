package org.firstinspires.ftc.teamcode.subsystems.Launcher;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/** Controle de RPM pelo PIDF de velocidade do hub, com calibração no Dashboard. */
@Config
public class LauncherPIDado {

    // Valores iniciais do LauncherVelocity; ajuste conforme o motor e seu encoder.
    // RPM e ticks por volta se referem ao mesmo eixo, antes de transmissões externas.
    public static volatile double TICKS_PER_REV = 28.0;
    public static volatile double MAX_RPM = 6000.0;

    // Ganhos do hub em ticks/s. São valores iniciais, que precisam de calibração.
    public static volatile double P = 1000.0;
    public static volatile double I = 0.0;
    public static volatile double D = 0.0;
    public static volatile double F = 11.7;

    private final DcMotorEx launcherMotor;
    private final Telemetry telemetry;
    private PIDFCoefficients appliedPIDF;
    private double targetRPM = 0.0;
    private String status = "Parado";

    public LauncherPIDado(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;

        launcherMotor = hardwareMap.get(DcMotorEx.class, "launcher_motor");
        launcherMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        launcherMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        launcherMotor.setPower(0.0);
        launcherMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        stop();
    }

    /**
     * Chame a cada iteração do TeleOp com o RPM desejado. O hub mantém a velocidade
     * pelo encoder e recebe o novo alvo imediatamente quando o RPM muda.
     * RPM zero, negativo ou não finito desliga o motor.
     */
    public void run(double rpm) {
        // Lê os valores atuais, para alterações no Dashboard valerem neste loop.
        double ticksPerRev = TICKS_PER_REV;
        double maxRPM = MAX_RPM;
        double p = P;
        double i = I;
        double d = D;
        double f = F;

        if (!Double.isFinite(ticksPerRev) || ticksPerRev <= 0.0
                || !Double.isFinite(maxRPM) || maxRPM <= 0.0
                || !isValidGain(p) || !isValidGain(i)
                || !isValidGain(d) || !isValidGain(f)) {
            stop();
            status = "Configuração inválida";
            return;
        }

        if (!Double.isFinite(rpm) || rpm <= 0.0) {
            stop();
            return;
        }

        // Atualiza os ganhos apenas quando mudam, evitando escritas repetidas no hub.
        if (appliedPIDF == null || appliedPIDF.p != p || appliedPIDF.i != i
                || appliedPIDF.d != d || appliedPIDF.f != f) {
            launcherMotor.setVelocityPIDFCoefficients(p, i, d, f);
            appliedPIDF = new PIDFCoefficients(p, i, d, f);
        }

        targetRPM = Math.min(rpm, maxRPM);
        launcherMotor.setVelocity(targetRPM * ticksPerRev / 60.0);
        status = "Ativo";
    }

    public void stop() {
        targetRPM = 0.0;
        launcherMotor.setVelocity(0.0);
        launcherMotor.setPower(0.0);
        status = "Parado";
    }

    public double getTargetRPM() {
        return targetRPM;
    }

    public double getCurrentRPM() {
        double ticksPerRev = TICKS_PER_REV;
        if (!Double.isFinite(ticksPerRev) || ticksPerRev <= 0.0) {
            return 0.0;
        }
        return launcherMotor.getVelocity() * 60.0 / ticksPerRev;
    }

    public void sendTelemetry() {
        double currentRPM = getCurrentRPM();
        telemetry.addData("Launcher RPM alvo", targetRPM);
        telemetry.addData("Launcher RPM medido", currentRPM);
        telemetry.addData("Launcher erro RPM", targetRPM - currentRPM);
        telemetry.addData("Launcher status", status);
    }

    private static boolean isValidGain(double gain) {
        return Double.isFinite(gain) && gain >= 0.0;
    }
}
