/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.TimedRobot;

import com.ctre.phoenix.motorcontrol.VictorSPXControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class Robot extends TimedRobot {
  private Joystick mainJoystick;
  private Joystick secondaryJoystick;

  private static int driveStyle = 0;

  private static final double driveEncoderConversionRatio = (6 * Math.PI) / 10.75;

  private static final int intakeMotorPort = 0;

  private static final int transporterID = 30;
  private static final int climberID = 31;

  private static final int rightShooterID = 40;
  private static final int leftShooterID = 41;

  private static final int leftRearID = 51;
  private static final int rightRearID = 52;
  private static final int leftFrontID = 53;
  private static final int rightFrontID = 54;

  private Talon intakeMotor;

  private VictorSPX transporterMotor;
  private VictorSPX climberMotor;

  private CANSparkMax rightShooterMotor;
  private CANSparkMax leftShooterMotor;

  private CANSparkMax leftRearMotor;
  private CANSparkMax rightRearMotor;
  private CANSparkMax leftFrontMotor;
  private CANSparkMax rightFrontMotor;

  private CANEncoder driveEncoder;

  @Override
  public void robotInit() {
    leftRearMotor = new CANSparkMax(leftRearID, MotorType.kBrushless);
    rightRearMotor = new CANSparkMax(rightRearID, MotorType.kBrushless);
    leftFrontMotor = new CANSparkMax(leftFrontID, MotorType.kBrushless);
    rightFrontMotor = new CANSparkMax(rightFrontID, MotorType.kBrushless);

    transporterMotor = new VictorSPX(transporterID);
    climberMotor = new VictorSPX(climberID);

    intakeMotor = new Talon(intakeMotorPort);

    rightShooterMotor = new CANSparkMax(rightShooterID, MotorType.kBrushless);
    leftShooterMotor = new CANSparkMax(leftShooterID, MotorType.kBrushless);

    mainJoystick = new Joystick(0);
    secondaryJoystick = new Joystick(1);

    driveEncoder = leftRearMotor.getEncoder();
    driveEncoder.setPositionConversionFactor(driveEncoderConversionRatio);
  }

  @Override
  public void autonomousPeriodic() {
    System.out.println(driveEncoder.getPosition());
  }

  @Override
  public void teleopPeriodic() {
    if(driveStyle == 0){
      if(secondaryJoystick.getRawButton(1)){
        rightShooterMotor.set(-1);
        leftShooterMotor.set(1);
      } else {
        rightShooterMotor.set(0);
        leftShooterMotor.set(0);
      }

      if(secondaryJoystick.getRawButton(3)){
        transporterMotor.set(VictorSPXControlMode.PercentOutput, .1);
      } else if(secondaryJoystick.getRawButton(2)) {
        transporterMotor.set(VictorSPXControlMode.PercentOutput,-.1);
      } else {
        transporterMotor.set(VictorSPXControlMode.PercentOutput, 0);
      }

      if(secondaryJoystick.getRawButton(5)){
        intakeMotor.set(.5);
      } else if(secondaryJoystick.getRawButton(6)) {
        intakeMotor.set(-.5);
      } else {
        intakeMotor.set(0);
      }

      if(secondaryJoystick.getRawAxis(2) > .9){
        climberMotor.set(VictorSPXControlMode.PercentOutput, .5);
      } else if (secondaryJoystick.getRawAxis(3) > .9){
        climberMotor.set(VictorSPXControlMode.PercentOutput, -.5);
      } else {
        climberMotor.set(VictorSPXControlMode.PercentOutput, 0);
      }


    } else {
      if(secondaryJoystick.getRawButton(5)){
        rightShooterMotor.set(-1);
        leftShooterMotor.set(1);
      } else {
        rightShooterMotor.set(0);
        leftShooterMotor.set(0);
      }

      if(secondaryJoystick.getRawButton(3)){
        transporterMotor.set(VictorSPXControlMode.PercentOutput, .1);
      } else if(secondaryJoystick.getRawButton(2)){
        transporterMotor.set(VictorSPXControlMode.PercentOutput, -.1);
      } else {
        transporterMotor.set(VictorSPXControlMode.PercentOutput, 0);
      }

      if(secondaryJoystick.getRawButton(6)){
        intakeMotor.set(.5);
      } else if(secondaryJoystick.getRawButton(1)){
        intakeMotor.set(-.5);
      } else {
        intakeMotor.set(0);
      }
    }
    /*
    // Drive
    leftFrontMotor.set(-controller.getRawAxis(1) / 2 + controller.getRawAxis(4) / 3);
    leftRearMotor.set(-controller.getRawAxis(1) / 2 + controller.getRawAxis(4) / 3);

    rightFrontMotor.set(controller.getRawAxis(1) / 2 + controller.getRawAxis(4) / 3);
    rightRearMotor.set(controller.getRawAxis(1) / 2 + controller.getRawAxis(4) / 3);

    // Shooter
    if (controller.getRawButton(1)) {
      rightShooterMotor.set(-1);
      leftShooterMotor.set(1);
    } else {
      rightShooterMotor.set(0);
      leftShooterMotor.set(0);
    }

    // Intake
    if (controller.getRawButton(5)) { // In
      intakeMotor.set(-.5);
      transporterMotor.set(VictorSPXControlMode.PercentOutput, .1);
    } else if (controller.getRawButton(6)) { // Out
      intakeMotor.set(.5);
      transporterMotor.set(VictorSPXControlMode.PercentOutput, -.1);
    } else {
      intakeMotor.set(0);
      transporterMotor.set(VictorSPXControlMode.PercentOutput, 0);
    }

    // Transporter
    if (controller.getRawButton(3)) { // Up
      transporterMotor.set(VictorSPXControlMode.PercentOutput, 0.5);
    } else if (controller.getRawButton(2)) { // Down
      transporterMotor.set(VictorSPXControlMode.PercentOutput, -0.5);
    } else {
      transporterMotor.set(VictorSPXControlMode.PercentOutput, 0);
    }
    */
  }
}
