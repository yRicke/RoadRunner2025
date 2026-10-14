package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LauncherPIDadoTest {

    private DcMotorEx motor;
    private Telemetry telemetry;
    private LauncherPIDado launcher;

    @Before
    public void setUp() {
        LauncherPIDado.TICKS_PER_REV = 28.0;
        LauncherPIDado.MAX_RPM = 6000.0;
        LauncherPIDado.P = 10.0;
        LauncherPIDado.I = 0.0;
        LauncherPIDado.D = 0.0;
        LauncherPIDado.F = 11.7;

        motor = mock(DcMotorEx.class);
        telemetry = mock(Telemetry.class);
        HardwareMap hardwareMap = mock(HardwareMap.class);
        when(hardwareMap.get(DcMotorEx.class, "launcher_motor")).thenReturn(motor);
        launcher = new LauncherPIDado(hardwareMap, telemetry);
    }

    @Test
    public void convertsRequestedRpmToEncoderTicksPerSecond() {
        launcher.run(3000);

        verify(motor).setVelocity(1400.0);
    }

    @Test
    public void initializesStoppedWithEncoderVelocityControl() {
        verify(motor).setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        verify(motor).setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        verify(motor).setVelocity(0.0);
        assertEquals(0.0, launcher.getTargetRPM(), 0.0);
    }

    @Test
    public void followsIncreasingAndDecreasingRpmTargets() {
        launcher.run(3000);
        launcher.run(4500);
        launcher.run(1800);

        InOrder commands = inOrder(motor);
        commands.verify(motor).setVelocity(1400.0);
        commands.verify(motor).setVelocity(2100.0);
        commands.verify(motor).setVelocity(840.0);
        assertEquals(1800.0, launcher.getTargetRPM(), 0.0);
    }

    @Test
    public void acceptsFractionalRpm() {
        launcher.run(3000.75);

        verify(motor).setVelocity(1400.35);
        assertEquals(3000.75, launcher.getTargetRPM(), 0.0);
    }

    @Test
    public void limitsTargetToConfiguredMaximum() {
        launcher.run(9000);

        verify(motor).setVelocity(2800.0);
        assertEquals(6000.0, launcher.getTargetRPM(), 0.0);
    }

    @Test
    public void appliesDashboardChangesToConversionAndLimitWhileRunning() {
        launcher.run(3000);
        LauncherPIDado.TICKS_PER_REV = 56.0;
        LauncherPIDado.MAX_RPM = 2000.0;
        launcher.run(3000);

        verify(motor).setVelocity(2000.0 * 56.0 / 60.0);
        assertEquals(2000.0, launcher.getTargetRPM(), 0.0);
    }

    @Test
    public void appliesInitialPidfAndDashboardEditsWithoutRestarting() {
        launcher.run(3000);
        LauncherPIDado.P = 12.0;
        LauncherPIDado.I = 1.0;
        LauncherPIDado.D = 0.5;
        LauncherPIDado.F = 12.3;
        launcher.run(3000);

        verify(motor).setVelocityPIDFCoefficients(10.0, 0.0, 0.0, 11.7);
        verify(motor).setVelocityPIDFCoefficients(12.0, 1.0, 0.5, 12.3);
    }

    @Test
    public void doesNotRewriteUnchangedPidfOnEveryLoop() {
        launcher.run(3000);
        launcher.run(4500);

        verify(motor, times(1)).setVelocityPIDFCoefficients(10.0, 0.0, 0.0, 11.7);
    }

    @Test
    public void stopsForNonpositiveAndNonfiniteRpm() {
        double[] invalidTargets = {0.0, -1000.0, Double.NaN,
                Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY};

        for (double rpm : invalidTargets) {
            launcher.run(3000);
            clearInvocations(motor);
            launcher.run(rpm);

            verify(motor).setVelocity(0.0);
            verify(motor).setPower(0.0);
            assertEquals(0.0, launcher.getTargetRPM(), 0.0);
        }
    }

    @Test
    public void stopClearsPreviousTargetAndAllowsRestart() {
        launcher.run(3000);
        clearInvocations(motor);
        launcher.stop();

        verify(motor).setVelocity(0.0);
        verify(motor).setPower(0.0);
        assertEquals(0.0, launcher.getTargetRPM(), 0.0);

        launcher.run(1500);
        verify(motor).setVelocity(700.0);
    }

    @Test
    public void convertsMeasuredEncoderVelocityToRpm() {
        when(motor.getVelocity()).thenReturn(1260.0);

        assertEquals(2700.0, launcher.getCurrentRPM(), 0.0001);
    }

    @Test
    public void reportsTargetMeasuredRpmAndErrorInTheSameUnit() {
        launcher.run(3000);
        when(motor.getVelocity()).thenReturn(1260.0);
        launcher.sendTelemetry();

        verify(telemetry).addData("Launcher RPM alvo", 3000.0);
        verify(telemetry).addData("Launcher RPM medido", 2700.0);
        verify(telemetry).addData("Launcher erro RPM", 300.0);
    }

    @Test
    public void stopsIfEncoderConfigurationBecomesInvalid() {
        launcher.run(3000);
        clearInvocations(motor);
        LauncherPIDado.TICKS_PER_REV = 0.0;
        launcher.run(3000);

        verify(motor).setVelocity(0.0);
        verify(motor).setPower(0.0);
        assertEquals(0.0, launcher.getCurrentRPM(), 0.0);
        assertEquals(0.0, launcher.getTargetRPM(), 0.0);
    }

    @Test
    public void stopsIfMaximumRpmConfigurationBecomesInvalid() {
        launcher.run(3000);
        clearInvocations(motor);
        LauncherPIDado.MAX_RPM = Double.POSITIVE_INFINITY;
        launcher.run(3000);

        verify(motor).setVelocity(0.0);
        verify(motor).setPower(0.0);
        assertEquals(0.0, launcher.getTargetRPM(), 0.0);
    }

    @Test
    public void stopsAndReportsInvalidPidfInsteadOfSendingItToTheHub() {
        launcher.run(3000);
        clearInvocations(motor);
        LauncherPIDado.P = Double.NaN;
        launcher.run(3000);
        launcher.sendTelemetry();

        verify(motor).setVelocity(0.0);
        verify(motor).setPower(0.0);
        verify(telemetry).addData("Launcher status", "Configuração inválida");
        verify(motor, never()).setVelocityPIDFCoefficients(
                anyDouble(), anyDouble(), anyDouble(), anyDouble());
    }
}
