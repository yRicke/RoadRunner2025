package org.firstinspires.ftc.teamcode.Auto;

import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Launcher;

/**
 * Autônomo para o lado Vermelho Sul (Red South) do campo.
 * Espelhado do AutoBlueSouth.
 * O robô começa na posição X=62, Y=12 e se move para Launch (X=50, Y=12),
 * e usa Second Launch (X=-12, Y=12) como ponto de rearmamento no lado oposto.
 * A sequência de pontuação é GPP -> PGP -> PPG.
 */
@Autonomous(name = "AutoRedSouth", group = "Autonomous")
public class AutoRedSouth extends LinearOpMode {

    // --- POSIÇÕES BASE (Espelhadas do AutoBlueSouth: Y positivo) ---
    final double startPositionX = 62;
    final double startPositionY = 12; // Invertido: -12 -> 12

    final double launchPositionX = 50;
    final double launchPositionY = 12; // Invertido: -12 -> 12

    final double secondLaunchPositionX = -12;
    final double secondLaunchPositionY = 12; // Invertido: -12 -> 12

    // Posições de Pontuação/Coleta (Backboard Side)
    final double ppgPositionX = -12;
    final double ppgPositionY = 56; // Invertido: -56 -> 56

    final double pgpPositionX = 14;
    final double pgpPositionY = 63; // Invertido: -63 -> 63

    final double gppPositionX = 36;
    final double gppPositionY = 63; // Invertido: -63 -> 63

    final double preY = 25; // Invertido: -25 -> 25 (Passagem intermediária)

    // Direções (Headings) - Ajustadas para o Quadrante Vermelho
    final double startPositionHeading = Math.toRadians(0); // Mantido em 0
    final double launchPositionHeading = Math.toRadians(-30); // Invertido: 30 -> -30
    final double secondLaunchPositionHeading = Math.toRadians(-45); // Invertido: 45 -> -45
    final double modifPositionHeading = Math.toRadians(90); // Invertido: 270 (Sul) -> 90 (Norte) para encarar o Backboard

    // Vetores de Posição
    final Vector2d startVector = new Vector2d(startPositionX, startPositionY);
    final Vector2d launchVector = new Vector2d(launchPositionX, launchPositionY);
    final Vector2d secondLaunchVector = new Vector2d(secondLaunchPositionX, secondLaunchPositionY);
    final Vector2d ppgVector = new Vector2d(ppgPositionX, ppgPositionY);
    final Vector2d pgpVector = new Vector2d(pgpPositionX, pgpPositionY);
    final Vector2d gppVector = new Vector2d(gppPositionX, gppPositionY);

    // Vetores de Pré-posição
    final Vector2d prePpgVector = new Vector2d(ppgPositionX, preY);
    final Vector2d prePgpVector = new Vector2d(pgpPositionX, preY);
    final Vector2d preGppVector = new Vector2d(gppPositionX, preY);

    // Poses Completas
    final Pose2d startPose = new Pose2d(startVector, startPositionHeading);
    final Pose2d launchPose = new Pose2d(launchVector, launchPositionHeading);
    final Pose2d secondLaunchPose = new Pose2d(secondLaunchVector, secondLaunchPositionHeading);
    final Pose2d ppgPose = new Pose2d(ppgVector, modifPositionHeading);
    final Pose2d pgpPose = new Pose2d(pgpVector, modifPositionHeading);
    final Pose2d gppPose = new Pose2d(gppVector, modifPositionHeading);

    @Override
    public void runOpMode(){

        Intake intake = new Intake(hardwareMap, telemetry);
        Launcher launcher = new Launcher(hardwareMap, telemetry);
        // Inicializa a tração com a pose inicial do robô
        MecanumDrive drive = new MecanumDrive(hardwareMap, startPose);

        // --- TRAJETÓRIAS ---

        // 1. Início para o 1º Ponto de Lançamento (Launch)
        TrajectoryActionBuilder goToLaunch = drive.actionBuilder(startPose)
                .strafeToLinearHeading(launchVector, launchPositionHeading);

        // 2. Launch para GPP (Ponto de Pontuação/Coleta mais à direita)
        TrajectoryActionBuilder goToGPP = drive.actionBuilder(launchPose)
                .strafeToLinearHeading(preGppVector, modifPositionHeading) // Move para a pré-posição
                .strafeTo(gppVector); // Entra na posição de pontuação

        // 3. GPP de volta para Launch
        TrajectoryActionBuilder returnToLaunchByGPP = drive.actionBuilder(gppPose)
                .strafeToLinearHeading(launchVector, launchPositionHeading);

        // 4. Launch para PGP (Ponto de Pontuação/Coleta central)
        TrajectoryActionBuilder goToPGP = drive.actionBuilder(launchPose)
                .strafeToLinearHeading(prePgpVector, modifPositionHeading) // Move para a pré-posição
                .strafeTo(pgpVector); // Entra na posição de pontuação

        // 5. PGP para o 2º Ponto de Lançamento (Second Launch - cruzando o campo)
        TrajectoryActionBuilder goToSecondLaunchByPGP = drive.actionBuilder(pgpPose)
                .strafeToLinearHeading(secondLaunchVector, secondLaunchPositionHeading);

        // 6. Second Launch para PPG (Ponto de Pontuação/Coleta mais à esquerda)
        TrajectoryActionBuilder goToPPG = drive.actionBuilder(secondLaunchPose)
                .strafeToLinearHeading(prePpgVector, modifPositionHeading) // Move para a pré-posição
                .strafeTo(ppgVector); // Entra na posição de pontuação

        // 7. PPG de volta para o 2º Ponto de Lançamento
        TrajectoryActionBuilder returnToSecondLaunchByPPG = drive.actionBuilder(ppgPose)
                .strafeToLinearHeading(secondLaunchVector, secondLaunchPositionHeading);


        // ======================================
        // SELEÇÃO DE POTÊNCIA (antes do start)
        // ======================================

        double flywheelPower = 0.75;

        telemetry.addLine("=== SELECIONE A VOLTAGEM ===");
        telemetry.update();

        // Loop de seleção antes de iniciar (usa o Gamepad 1)
        while (!isStarted() && !isStopRequested()) {
            if (gamepad1.dpad_up) {
                flywheelPower = Math.min(1.0, flywheelPower + 0.01); // Limita a 1.0
                sleep(200);
            } else if (gamepad1.dpad_down) {
                flywheelPower = Math.max(0.0, flywheelPower - 0.01); // Limita a 0.0
                sleep(200);
            }

            telemetry.clear();
            telemetry.addData("Potência correspondente", "%.3f", flywheelPower);
            telemetry.addLine("Use D-Pad ↑↓ para alterar (Valor atual: " + String.format("%.3f", flywheelPower) + ")");
            telemetry.addLine("Pressione START para iniciar");
            telemetry.update();
        }

        waitForStart();

        // --- SEQUÊNCIA DE AÇÕES ---
        Actions.runBlocking(
                new ParallelAction(
                        // Ações paralelas
                        intake.onAuto(),
                        launcher.onAuto(flywheelPower),

                        // Ações sequenciais: Movimentação e Lançamento
                        new SequentialAction(
                                // 1. Lançamento GPP
                                goToLaunch.build(), // Move para a 1ª posição de lançamento
                                launcher.launch(),  // Lança 1º elemento
                                goToGPP.build(),    // Vai para GPP
                                returnToLaunchByGPP.build(), // Volta para Launch

                                // 2. Lançamento PGP
                                launcher.launch(),  // Lança 2º elemento
                                goToPGP.build(),    // Vai para PGP
                                goToSecondLaunchByPGP.build(), // Move para 2ª posição de lançamento

                                // 3. Lançamento PPG
                                launcher.launch(),  // Lança 3º elemento
                                goToPPG.build(),    // Vai para PPG
                                returnToSecondLaunchByPPG.build(), // Volta para Second Launch

                                // 4. Lançamento final (se houver 4ª peça)
                                launcher.launch() // Lança 4º elemento (parado na Second Launch)
                        )
                )
        );
    }
}