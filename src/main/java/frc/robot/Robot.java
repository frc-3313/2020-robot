/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.DigitalInput;
//import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

import com.ctre.phoenix.motorcontrol.VictorSPXControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class Robot extends TimedRobot {
  /*
    Configuration
  */

  // Encoders
  private static final double driveEncoderConversionRatio = (6 * Math.PI) / 10.75;

  // DIO Ports
  private static final int intakeSensorPort = 0;

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

  // DIO
  private DigitalInput intakeSensor;

  // Motor Controllers
  private Talon intakeMotor;

  private VictorSPX conveyorMotor;
  private CANSparkMax climberMotor;

  private CANSparkMax rightShooterMotor;
  private CANSparkMax leftShooterMotor;

  private CANSparkMax leftRearMotor;
  private CANSparkMax rightRearMotor;
  private CANSparkMax leftFrontMotor;
  private CANSparkMax rightFrontMotor;

  // Encoders
  private CANEncoder driveEncoder;

  // Shuffleboard
  private ShuffleboardTab sbTab;
  private NetworkTableEntry sbDriveTypeEntry;

  @Override
  public void robotInit() {
    // Joysticks
    mainJoystick = new Joystick(0);
    secondaryJoystick = new Joystick(1);

    // DIO
    intakeSensor = new DigitalInput(intakeSensorPort);

    // Motor Controllers
    intakeMotor = new Talon(intakeMotorPort);

    conveyorMotor = new VictorSPX(conveyorID);
    climberMotor = new CANSparkMax(climberID, MotorType.kBrushless);

    rightShooterMotor = new CANSparkMax(rightShooterID, MotorType.kBrushless);
    leftShooterMotor = new CANSparkMax(leftShooterID, MotorType.kBrushless);

    leftRearMotor = new CANSparkMax(leftRearID, MotorType.kBrushless);
    rightRearMotor = new CANSparkMax(rightRearID, MotorType.kBrushless);
    leftFrontMotor = new CANSparkMax(leftFrontID, MotorType.kBrushless);
    rightFrontMotor = new CANSparkMax(rightFrontID, MotorType.kBrushless);

    // Encoders
    driveEncoder = leftRearMotor.getEncoder();
    driveEncoder.setPositionConversionFactor(driveEncoderConversionRatio);

    // Shuffleboard
    sbTab = Shuffleboard.getTab("Drive");
    sbDriveTypeEntry = sbTab.add("Secondary Driver Type", "sarah").getEntry();
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
    if(sbDriveTypeEntry.getString("sarah").equals("sarah")){
      /*
        Sarah's Control Scheme
      */

      // Shooter
      if(secondaryJoystick.getRawButton(1)){
        rightShooterMotor.set(-.75);
        leftShooterMotor.set(.75);
        conveyorMotor.set(VictorSPXControlMode.PercentOutput, 0.3);
      } else {
        rightShooterMotor.set(0);
        leftShooterMotor.set(0);
        conveyorMotor.set(VictorSPXControlMode.PercentOutput, 0);
      }

      // Conveyor
      /*
      if(secondaryJoystick.getRawButton(3)){
        conveyorMotor.set(VictorSPXControlMode.PercentOutput, .5);
      } else if(secondaryJoystick.getRawButton(2)) {
        conveyorMotor.set(VictorSPXControlMode.PercentOutput, -.5);
      } else {
        conveyorMotor.set(VictorSPXControlMode.PercentOutput, 0);
      }
      */

      // Intake
      if(secondaryJoystick.getRawButton(5)){
        intakeMotor.set(.5);

        
      } else if(secondaryJoystick.getRawButton(6)) {
        intakeMotor.set(-.5);

        if(intakeSensor.get()) {
          conveyorMotor.set(VictorSPXControlMode.PercentOutput, .5);
        } else {
          conveyorMotor.set(VictorSPXControlMode.PercentOutput, 0);
        }
      } else {
        intakeMotor.set(0);
      }

      // Climber
      if(secondaryJoystick.getRawAxis(2) > .9){
        climberMotor.set(1);
      } else if (secondaryJoystick.getRawAxis(3) > .9){
        climberMotor.set(-1);
      } else {
        climberMotor.set(0);
      }

      if(secondaryJoystick.getRawButton(2)){
        conveyorMotor.set(VictorSPXControlMode.PercentOutput, -.5);
      }
    } else {
      /*
        Rachel's Control Scheme
      */

      // Shooter
      if(secondaryJoystick.getRawButton(5)){
        rightShooterMotor.set(-1);
        leftShooterMotor.set(1);
        conveyorMotor.set(VictorSPXControlMode.PercentOutput, 0.75);
      } else {
        rightShooterMotor.set(0);
        leftShooterMotor.set(0);
        conveyorMotor.set(VictorSPXControlMode.PercentOutput, 0);
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
        climberMotor.set(1);
      } else if (secondaryJoystick.getRawAxis(3) > .9){
        climberMotor.set(-1);
      } else {
        climberMotor.set(0);
      }
    }
  }
}
