// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import frc.robot.sensors.RomiGyro;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds;

public class Drivetrain extends SubsystemBase {
  private static final double kCountsPerRevolution = 1440.0;
  private static final double kWheelDiameterInch = 2.75591; // 70 mm

  // The Romi has the left and right motors set to
  // PWM channels 0 and 1 respectively
  private final Spark m_leftMotor = new Spark(0);
  private final Spark m_rightMotor = new Spark(1);

  // The Romi has onboard encoders that are hardcoded
  // to use DIO pins 4/5 and 6/7 for the left and right
  private final Encoder m_leftEncoder = new Encoder(4, 5);
  private final Encoder m_rightEncoder = new Encoder(6, 7);

  // Set up the differential drive controller
  private final DifferentialDrive m_diffDrive = new DifferentialDrive(m_leftMotor, m_rightMotor);

  // Set up the RomiGyro
  private final RomiGyro m_gyro = new RomiGyro();

  // Set up the BuiltInAccelerometer
  private final BuiltInAccelerometer m_accelerometer = new BuiltInAccelerometer();

  // Odometry class for tracking robot pose
  private final DifferentialDriveOdometry m_odometry;

  /** Creates a new Drivetrain. */
  public Drivetrain() {
    // Use inches as unit for encoder distances
    m_leftEncoder.setDistancePerPulse((Math.PI * kWheelDiameterInch) / kCountsPerRevolution);
    m_rightEncoder.setDistancePerPulse((Math.PI * kWheelDiameterInch) / kCountsPerRevolution);
    resetEncoders();
    m_odometry = new DifferentialDriveOdometry(getGyroRotation2d());
  }

  public void arcadeDrive(double xaxisSpeed, double zaxisRotate) {
    m_diffDrive.arcadeDrive(xaxisSpeed, zaxisRotate);
  }

  /**
   * Controls the left and right sides of the drive directly with voltages.
   *
   * @param leftVolts  the commanded left output
   * @param rightVolts the commanded right output
   */
  public void tankDriveVolts(double leftVolts, double rightVolts) {
    m_leftMotor.setVoltage(leftVolts);
    m_rightMotor.setVoltage(-rightVolts);
    m_diffDrive.feed();
  }

  public void resetEncoders() {
    m_leftEncoder.reset();
    m_rightEncoder.reset();
  }

  public int getLeftEncoderCount() {
    return m_leftEncoder.get();
  }

  public int getRightEncoderCount() {
    return m_rightEncoder.get();
  }

  public double getLeftDistanceInch() {
    return m_leftEncoder.getDistance();
  }

  public double getRightDistanceInch() {
    return m_rightEncoder.getDistance();
  }

  public double getAverageDistanceInch() {
    return (getLeftDistanceInch() + getRightDistanceInch()) / 2.0;
  }

  


  /**
   * The acceleration in the X-axis.
   *
   * @return The acceleration of the Romi along the X-axis in Gs
   */
  public double getAccelX() {
    return m_accelerometer.getX();
  }

  /**
   * The acceleration in the Y-axis.
   *
   * @return The acceleration of the Romi along the Y-axis in Gs
   */
  public double getAccelY() {
    return m_accelerometer.getY();
  }

  /**
   * The acceleration in the Z-axis.
   *
   * @return The acceleration of the Romi along the Z-axis in Gs
   */
  public double getAccelZ() {
    return m_accelerometer.getZ();
  }


    /**
   * Current angle of the Romi as a Rotation2d object.
   *
   * @return The current angle of the Romi as a Rotation2d Object
   */
  public Rotation2d getGyroRotation2d() {
    return new Rotation2d(Math.toRadians(m_gyro.getAngleZ()));
  }

  /**
   * Current angle of the Romi around the X-axis.
   *
   * @return The current angle of the Romi in degrees
   */
  public double getGyroAngleX() {
    return m_gyro.getAngleX();
  }

  /**
   * Current angle of the Romi around the Y-axis.
   *
   * @return The current angle of the Romi in degrees
   */
  public double getGyroAngleY() {
    return m_gyro.getAngleY();
  }

  /**
   * Current angle of the Romi around the Z-axis.
   *
   * @return The current angle of the Romi in degrees
   */
  public double getGyroAngleZ() {
    return m_gyro.getAngleZ();
  }

  /** Reset the gyro. */
  public void resetGyro() {
    m_gyro.reset();
  }

  /**
   * Returns the currently-estimated pose of the robot.
   *
   * @return The pose.
   */
  public Pose2d getPose() {
    //TODO: Check units 
    return m_odometry.getPoseMeters();
  }

  /**
   * Returns the current wheel speeds of the robot.
   *
   * @return The current wheel speeds.
   */
  public DifferentialDriveWheelSpeeds getWheelSpeeds() {
    return new DifferentialDriveWheelSpeeds(m_leftEncoder.getRate(), m_rightEncoder.getRate());
  }
  
  /**
   * Resets the odometry to the specified pose.
   *
   * @param pose The pose to which to set the odometry.
   */
  public void resetOdometry(Pose2d pose) {
    resetEncoders();
    m_odometry.resetPosition(pose, getGyroRotation2d());
  }
  
  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    // Update the odometry in the periodic block
    m_odometry.update(getGyroRotation2d(), m_leftEncoder.getDistance(), m_rightEncoder.getDistance());
  }

  

}
