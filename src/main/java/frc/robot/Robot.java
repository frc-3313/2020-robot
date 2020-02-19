/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.TimedRobot;

import com.ctre.phoenix.motorcontrol.VictorSPXControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class Robot extends TimedRobot {
  private ConjoinedController controller;

  private static final double driveEncoderConversionRatio = (6 * Math.PI) / 10.75;

  private static final int intakeMotorPort = 0;

  private static final int transporterID = 30;

  private static final int rightShooterID = 40;
  private static final int leftShooterID = 41;

  private static final int leftRearID = 51;
  private static final int rightRearID = 52;
  private static final int leftFrontID = 53;
  private static final int rightFrontID = 54;

  private Talon intakeMotor;

  private VictorSPX transporterMotor;

  private CANSparkMax rightShooterMotor;
  private CANSparkMax leftShooterMotor;

  private CANSparkMax leftRearMotor;
  private CANSparkMax rightRearMotor;
  private CANSparkMax leftFrontMotor;
  private CANSparkMax rightFrontMotor;

  private CANEncoder driveEncoder;

  private DigitalInput intakeSensor;

  @Override
  public void robotInit() {
    leftRearMotor = new CANSparkMax(leftRearID, MotorType.kBrushless);
    rightRearMotor = new CANSparkMax(rightRearID, MotorType.kBrushless);
    leftFrontMotor = new CANSparkMax(leftFrontID, MotorType.kBrushless);
    rightFrontMotor = new CANSparkMax(rightFrontID, MotorType.kBrushless);

    transporterMotor = new VictorSPX(transporterID);

    intakeMotor = new Talon(intakeMotorPort);

    rightShooterMotor = new CANSparkMax(rightShooterID, MotorType.kBrushless);
    leftShooterMotor = new CANSparkMax(leftShooterID, MotorType.kBrushless);

    controller = new ConjoinedController(0, 1);

    intakeSensor = new DigitalInput(0);

    driveEncoder = leftRearMotor.getEncoder();
    driveEncoder.setPositionConversionFactor(driveEncoderConversionRatio);
  }

  @Override
  public void autonomousPeriodic() {
    System.out.println(driveEncoder.getPosition());
  }

  @Override
  public void teleopPeriodic() {
    System.out.println(intakeSensor.get());

    // Drive
    leftFrontMotor.set(-controller.getRawAxis(1) / 2 + controller.getRawAxis(4) / 3);
    leftRearMotor.set(-controller.getRawAxis(1) / 2 + controller.getRawAxis(4) / 3);

    rightFrontMotor.set(controller.getRawAxis(1) / 2 + controller.getRawAxis(4) / 3);
    rightRearMotor.set(controller.getRawAxis(1) / 2 + controller.getRawAxis(4) / 3);

    // Shooter
    if (controller.getRawButton(1)) {
      rightShooterMotor.set(-.5);
      leftShooterMotor.set(.5);
    } else {
      rightShooterMotor.set(0);
      leftShooterMotor.set(0);
    }

    // Intake
    if (controller.getRawButton(2)) { // In
      intakeMotor.set(-.5);
      transporterMotor.set(VictorSPXControlMode.PercentOutput, 1);
    } else if (controller.getRawButton(3)) { // Out
      intakeMotor.set(.5);
      transporterMotor.set(VictorSPXControlMode.PercentOutput, -1);
    } else {
      intakeMotor.set(0);
      transporterMotor.set(VictorSPXControlMode.PercentOutput, 0);
    }

    // Transporter
    if (controller.getRawButton(5)) { // Up
      transporterMotor.set(VictorSPXControlMode.PercentOutput, 0.5);
    } else if (controller.getRawButton(6)) { // Down
      transporterMotor.set(VictorSPXControlMode.PercentOutput, -0.5);
    } else {
     // transporterMotor.set(VictorSPXControlMode.PercentOutput, 0);
    }
  }
}
