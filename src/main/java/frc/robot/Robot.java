/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
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
   * Configuration
   */

  // Encoders
  private static final double driveEncoderConversionRatio = (6 * Math.PI) / 10.75;

  // DIO Ports
  private static final int intakeSensorPort = 0;

  // PWM Ports
  private static final int intakeMotorPort = 0;
  private static final int climberLockMotorPort = 1;

  // CAN IDs
  private static final int conveyorID = 30;
  private static final int climberID = 31;

  private static final int rightShooterID = 40;
  private static final int leftShooterID = 41;

  private static final int leftRearID = 51;
  private static final int rightRearID = 52;
  private static final int leftFrontID = 53;
  private static final int rightFrontID = 54;

  // Misc
  private static final double autoDriveSpeed = 0.1;

  /*
   * Components
   */
  // Joysticks
  private Joystick mainJoystick;
  private Joystick secondaryJoystick;

  // DIO
  private DigitalInput intakeSensor;

  // Motor Controllers
  private Talon intakeMotor;
  private Talon climberLockMotor;

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

  // Limelight
  private NetworkTable limelightTable;

  // Misc
  int ticksToConveyorStop = 0;
  int currentStoredBalls = 0;
  int ticksToShooterFullSpeed = -1;
  boolean updatedBallCount = false;

  @Override
  public void robotInit() {
    // Joysticks
    mainJoystick = new Joystick(0);
    secondaryJoystick = new Joystick(1);

    // DIO
    intakeSensor = new DigitalInput(intakeSensorPort);

    // Motor Controllers
    intakeMotor = new Talon(intakeMotorPort);
    climberLockMotor = new Talon(climberLockMotorPort);

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
    sbDriveTypeEntry = sbTab.add("Current Balls", 0).getEntry();

    // Limelight
    limelightTable = NetworkTableInstance.getDefault().getTable("limelight");
  }

  @Override
  public void autonomousInit() {
    driveEncoder.setPosition(0);
  }

  @Override
  public void autonomousPeriodic() {
    if(driveEncoder.getPosition() < 12){
      leftFrontMotor.set(autoDriveSpeed);
      leftRearMotor.set(autoDriveSpeed);
  
      rightFrontMotor.set(-autoDriveSpeed);
      rightRearMotor.set(-autoDriveSpeed);
    } else {
      leftFrontMotor.set(0);
      leftRearMotor.set(0);
  
      rightFrontMotor.set(0);
      rightRearMotor.set(0);
    }
  }

  @Override
  public void teleopPeriodic() {
    /*
     * Shuffleboard
     */
    sbDriveTypeEntry.setNumber(currentStoredBalls);

    /*
     * Robot Drive Code
     */
    leftFrontMotor.set(-mainJoystick.getRawAxis(1) / 2 + mainJoystick.getRawAxis(4) / 3);
    leftRearMotor.set(-mainJoystick.getRawAxis(1) / 2 + mainJoystick.getRawAxis(4) / 3);

    rightFrontMotor.set(mainJoystick.getRawAxis(1) / 2 + mainJoystick.getRawAxis(4) / 3);
    rightRearMotor.set(mainJoystick.getRawAxis(1) / 2 + mainJoystick.getRawAxis(4) / 3);

    // Shooter
    if (secondaryJoystick.getRawButton(1)) {
      rightShooterMotor.set(-.5);
      leftShooterMotor.set(.5);

      if(ticksToShooterFullSpeed == 0 ){
        conveyorMotor.set(VictorSPXControlMode.PercentOutput, 0.4);
      } else if(ticksToShooterFullSpeed == -1){
        ticksToShooterFullSpeed = 30;
      } else {
        ticksToShooterFullSpeed--;
      }
      
      currentStoredBalls = 0;
    } else {
      rightShooterMotor.set(0);
      leftShooterMotor.set(0);
      conveyorMotor.set(VictorSPXControlMode.PercentOutput, 0);
      ticksToShooterFullSpeed = -1;
    }

    // Intake
    if (secondaryJoystick.getRawButton(5)) {
      intakeMotor.set(.5);
    } else if (secondaryJoystick.getRawButton(6)) {
      if (intakeSensor.get()) {
        ticksToConveyorStop = 2;
        intakeMotor.set(0);

        if(currentStoredBalls == 3){
          ticksToConveyorStop = 1;
        } else {
          if(!updatedBallCount){
            currentStoredBalls++;
            updatedBallCount = true;
          }
        }
      } else {
        conveyorMotor.set(VictorSPXControlMode.PercentOutput, 0);
        updatedBallCount = false;

        if(currentStoredBalls == 3){
          intakeMotor.set(0);
        } else {
          intakeMotor.set(-.5);
        }
      }

      if (ticksToConveyorStop > 0) {
        intakeMotor.set(0);
        conveyorMotor.set(VictorSPXControlMode.PercentOutput, .5);
        ticksToConveyorStop--;
      } else {
        conveyorMotor.set(VictorSPXControlMode.PercentOutput, 0);
      }
    } else {
      intakeMotor.set(0);
    }

    // Climber
    if (secondaryJoystick.getRawAxis(2) > .9) {
      climberMotor.set(1);
    } else if (secondaryJoystick.getRawAxis(3) > .9) {
      climberMotor.set(-1);
    } else {
      climberMotor.set(0);
    }

    // Climber Lock
    if (secondaryJoystick.getPOV()==0) {
      climberLockMotor.set(1);
    } else {
      climberLockMotor.set(0);
    }
    
    if (secondaryJoystick.getRawButton(2)) {
      conveyorMotor.set(VictorSPXControlMode.PercentOutput, -.5);
    }

    // Limelight
    if (secondaryJoystick.getRawButton(4)) {
      limelightTable.getEntry("ledMode").setNumber(3);
      Number targetOffsetAngle_Horizontal = limelightTable.getEntry("tx").getNumber(0);

      leftFrontMotor.set(0.01 * targetOffsetAngle_Horizontal.doubleValue());
      leftRearMotor.set(0.01 * targetOffsetAngle_Horizontal.doubleValue());
      rightFrontMotor.set(0.01 * targetOffsetAngle_Horizontal.doubleValue());
      rightRearMotor.set(0.01 * targetOffsetAngle_Horizontal.doubleValue());   
    } else {
      limelightTable.getEntry("ledMode").setNumber(1);
    }
  }
}
