/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

//import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.TimedRobot;

import com.ctre.phoenix.motorcontrol.VictorSPXControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class Robot extends TimedRobot {
  /*
    Configuration
  */
  private static int driveStyle = 0;

  // Encoders
  private static final double driveEncoderConversionRatio = (6 * Math.PI) / 10.75;

  // PWM Ports
  private static final int intakeMotorPort = 0;

  // CAN IDs
  private static final int conveyorID = 30;
  private static final int climberID = 31;

  private static final int rightShooterID = 40;
  private static final int leftShooterID = 41;

  private static final int leftRearID = 51;
  private static final int rightRearID = 52;
  private static final int leftFrontID = 53;
  private static final int rightFrontID = 54;

  /*
    Components
  */
  // Joysticks
  private Joystick mainJoystick;
  private Joystick secondaryJoystick;

  // Motor Controllers
  private Talon intakeMotor;

  private VictorSPX conveyorMotor;
  private VictorSPX climberMotor;

  private CANSparkMax rightShooterMotor;
  private CANSparkMax leftShooterMotor;

  private CANSparkMax leftRearMotor;
  private CANSparkMax rightRearMotor;
  private CANSparkMax leftFrontMotor;
  private CANSparkMax rightFrontMotor;

  // Encoders
  private CANEncoder driveEncoder;

  @Override
  public void robotInit() {
    // Joysticks
    mainJoystick = new Joystick(0);
    secondaryJoystick = new Joystick(1);

    // Motor Controllers
    intakeMotor = new Talon(intakeMotorPort);

    conveyorMotor = new VictorSPX(conveyorID);
    climberMotor = new VictorSPX(climberID);

    rightShooterMotor = new CANSparkMax(rightShooterID, MotorType.kBrushless);
    leftShooterMotor = new CANSparkMax(leftShooterID, MotorType.kBrushless);

    leftRearMotor = new CANSparkMax(leftRearID, MotorType.kBrushless);
    rightRearMotor = new CANSparkMax(rightRearID, MotorType.kBrushless);
    leftFrontMotor = new CANSparkMax(leftFrontID, MotorType.kBrushless);
    rightFrontMotor = new CANSparkMax(rightFrontID, MotorType.kBrushless);

    // Encoders
    driveEncoder = leftRearMotor.getEncoder();
    driveEncoder.setPositionConversionFactor(driveEncoderConversionRatio);
  }

  @Override
  public void autonomousInit() {
    
  }

  @Override
  public void autonomousPeriodic() {
    
  }

  @Override
  public void teleopPeriodic() {
    /*
      Robot Drive Code
    */
    leftFrontMotor.set(-mainJoystick.getRawAxis(1) / 2 + mainJoystick.getRawAxis(4) / 3);
    leftRearMotor.set(-mainJoystick.getRawAxis(1) / 2 + mainJoystick.getRawAxis(4) / 3);

    rightFrontMotor.set(mainJoystick.getRawAxis(1) / 2 + mainJoystick.getRawAxis(4) / 3);
    rightRearMotor.set(mainJoystick.getRawAxis(1) / 2 + mainJoystick.getRawAxis(4) / 3);

    /*
      Split code for 2 different drive styles
    */
    if(driveStyle == 0){
      /*
        Sarah's Control Scheme
      */

      // Shooter
      if(secondaryJoystick.getRawButton(1)){
        rightShooterMotor.set(-1);
        leftShooterMotor.set(1);
      } else {
        rightShooterMotor.set(0);
        leftShooterMotor.set(0);
      }

      // Conveyor
      if(secondaryJoystick.getRawButton(3)){
        conveyorMotor.set(VictorSPXControlMode.PercentOutput, .1);
      } else if(secondaryJoystick.getRawButton(2)) {
        conveyorMotor.set(VictorSPXControlMode.PercentOutput,-.1);
      } else {
        conveyorMotor.set(VictorSPXControlMode.PercentOutput, 0);
      }

      // Intake
      if(secondaryJoystick.getRawButton(5)){
        intakeMotor.set(.5);
      } else if(secondaryJoystick.getRawButton(6)) {
        intakeMotor.set(-.5);
      } else {
        intakeMotor.set(0);
      }

      // Climber
      if(secondaryJoystick.getRawAxis(2) > .9){
        climberMotor.set(VictorSPXControlMode.PercentOutput, 1);
      } else if (secondaryJoystick.getRawAxis(3) > .9){
        climberMotor.set(VictorSPXControlMode.PercentOutput, -1);
      } else {
        climberMotor.set(VictorSPXControlMode.PercentOutput, 0);
      }
    } else {
      /*
        Rachel's Control Scheme
      */

      // Shooter
      if(secondaryJoystick.getRawButton(5)){
        rightShooterMotor.set(-1);
        leftShooterMotor.set(1);
      } else {
        rightShooterMotor.set(0);
        leftShooterMotor.set(0);
      }

      // Conveyor
      if(secondaryJoystick.getRawButton(3)){
        conveyorMotor.set(VictorSPXControlMode.PercentOutput, .1);
      } else if(secondaryJoystick.getRawButton(2)){
        conveyorMotor.set(VictorSPXControlMode.PercentOutput, -.1);
      } else {
        conveyorMotor.set(VictorSPXControlMode.PercentOutput, 0);
      }

      // Intake
      if(secondaryJoystick.getRawButton(6)){
        intakeMotor.set(.5);
      } else if(secondaryJoystick.getRawButton(1)){
        intakeMotor.set(-.5);
      } else {
        intakeMotor.set(0);
      }

      // Climber
      if(secondaryJoystick.getRawAxis(2) > .9){
        climberMotor.set(VictorSPXControlMode.PercentOutput, 1);
      } else if (secondaryJoystick.getRawAxis(3) > .9){
        climberMotor.set(VictorSPXControlMode.PercentOutput, -1);
      } else {
        climberMotor.set(VictorSPXControlMode.PercentOutput, 0);
      }
    }
  }
}
