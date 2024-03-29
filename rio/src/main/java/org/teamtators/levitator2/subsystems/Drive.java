package org.teamtators.levitator2.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.SpeedController;
import org.teamtators.common.config.Configurable;
import org.teamtators.common.config.helpers.CtreMotorControllerGroupConfig;
import org.teamtators.common.config.helpers.SpeedControllerConfig;
import org.teamtators.common.config.helpers.SpeedControllerGroupConfig;
import org.teamtators.common.control.*;
import org.teamtators.common.drive.*;
import org.teamtators.common.hw.ADXRS453;
import org.teamtators.common.hw.CtreMotorControllerGroup;
import org.teamtators.common.hw.SRXEncoder;
import org.teamtators.common.hw.SpeedControllerGroup;
import org.teamtators.common.math.Pose2d;
import org.teamtators.common.math.Rotation;
import org.teamtators.common.math.Translation2d;
import org.teamtators.common.scheduler.RobotState;
import org.teamtators.common.scheduler.Subsystem;
import org.teamtators.common.tester.ManualTestGroup;
import org.teamtators.common.tester.components.*;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * Has 2 sets of wheels (on the left and right) which can be independently controlled with motors. Also has 2
 * encoders and a gyroscope to measure the movements of the robot
 */
public class Drive extends Subsystem implements Configurable<Drive.Config>, TankDrive {

    public static final Predicate<TrapezoidalProfileFollower> DEFAULT_PREDICATE = ControllerPredicates.finished();
    private CtreMotorControllerGroup leftMotor;
    private CtreMotorControllerGroup rightMotor;
    private SRXEncoder rightEncoder;
    private SRXEncoder leftEncoder;
    private ADXRS453 gyro;
    private PidController rotationController = new PidController("Drive.rotationController");

    private TrapezoidalProfileFollower straightMotionFollower = new TrapezoidalProfileFollower("Drive.straightMotionFollower");
    private PidController yawAngleController = new PidController("Drive.yawAngleController");
    private PidController leftController = new PidController("Drive.leftController");
    private PidController rightController = new PidController("Drive.rightController");
    private OutputController outputController = new OutputController();

    private TrapezoidalProfileFollower rotationMotionFollower =
            new TrapezoidalProfileFollower("Drive.rotationMotionFollower");

    private TankKinematics tankKinematics;
    private PoseEstimator poseEstimator = new PoseEstimator(this);
    private DriveSegmentsFollower driveSegmentsFollower = new DriveSegmentsFollower(this);

    private Config config;
    private double speed;

    private DriveMode driveMode = DriveMode.Stop;

    public Drive() {
        super("Drive");

        rotationController.setInputProvider(this::getYawAngle);
        rotationController.setOutputConsumer((double output) -> {
            outputController.setStraightOutput(speed, false);
            outputController.setRotationOutput(output);
        });

        straightMotionFollower.setPositionProvider(this::getCenterDistance);
        straightMotionFollower.setVelocityProvider(this::getCenterRate);
        straightMotionFollower.setOutputConsumer(outputController::setStraightOutput);

        yawAngleController.setInputProvider(this::getYawAngle);
        yawAngleController.setOutputConsumer(outputController::setRotationOutput);

        rotationMotionFollower.setPositionProvider(this::getYawAngle);
        rotationMotionFollower.setVelocityProvider(this::getYawRate);
        rotationMotionFollower.setOutputConsumer(outputController::setRotationOutput);

        leftController.setInputProvider(this::getLeftRate);
        leftController.setOutputConsumer(outputController::setLeftOutput);
        rightController.setInputProvider(this::getRightRate);
        rightController.setOutputConsumer(outputController::setRightOutput);
    }

    /**
     * Drives with certain powers
     *
     * @param leftPower  the power for the left side
     * @param rightPower the power for the right side
     */
    public void drivePowers(double leftPower, double rightPower) {
        setDriveMode(DriveMode.Power);
        setPowers(leftPower, rightPower);
    }

    private void stopPowers() {
        setPowers(0, 0);
        driveMode = DriveMode.Stop;
    }

    public void driveSpeeds(double leftSpeed, double rightSpeed) {
        setDriveMode(DriveMode.Speeds);
        setSpeeds(leftSpeed, rightSpeed);
        leftController.start();
        rightController.start();
    }

    private void stopSpeeds() {
        leftController.stop();
        rightController.stop();
        stopPowers();
    }

    /**
     * Drives with a certain heading (angle) and speed
     *
     * @param heading in degrees
     * @param speed   in inches/second
     */
    public void driveHeading(double heading, double speed) {
        setDriveMode(DriveMode.Heading);
        rotationController.setSetpoint(heading);
        outputController.setStraightOutput(speed, false);
        this.speed = speed;

        rotationController.start();
        outputController.start();
    }

    private void stopHeading() {
        rotationController.stop();
        outputController.setStraightOutput(0, true);
        outputController.stop();
        stopPowers();
    }

    public void driveStraightProfile(double heading, double distance) {
        setDriveMode(DriveMode.StraightProfile);
        yawAngleController.setSetpoint(heading);
        straightMotionFollower.moveDistance(distance);

        straightMotionFollower.start();
        yawAngleController.start();
        outputController.start();
    }

    private void stopStraightProfile() {
        straightMotionFollower.stop();
        yawAngleController.stop();
        outputController.stop();
        stopPowers();
    }

    public void driveRotationProfile(double heading) {
        setDriveMode(DriveMode.RotationProfile);
        rotationMotionFollower.moveToPosition(heading);
        rotationMotionFollower.start();
        outputController.start();
    }

    private void stopRotationProfile() {
        rotationMotionFollower.stop();
        outputController.stop();
        stopPowers();
    }

    public void driveArcProfile(double arcLength, double endAngle) {
        setDriveMode(DriveMode.ArcProfile);
        straightMotionFollower.moveDistance(arcLength);
        rotationMotionFollower.moveToPosition(endAngle);

        straightMotionFollower.start();
        rotationMotionFollower.start();
        outputController.start();
    }

    private void stopArcProfile() {
        straightMotionFollower.stop();
        rotationMotionFollower.stop();
        outputController.stop();
        stopPowers();
    }

    public void driveSegments(DriveSegments segments) {
        setDriveMode(DriveMode.Segments);
        driveSegmentsFollower.setSegments(segments);
        driveSegmentsFollower.start();
        leftController.start();
        rightController.start();
        outputController.start();
    }

    private void stopSegments() {
        driveSegmentsFollower.stop();
        leftController.stop();
        rightController.stop();
        outputController.stop();
        stopPowers();
    }

    private void setDriveMode(DriveMode mode) {
        if (this.driveMode != mode) {
            stop();
        }
        this.driveMode = mode;
    }

    public void stop() {
        switch (driveMode) {
            case Stop:
            case Power:
                stopPowers();
                break;
            case Speeds:
                stopSpeeds();
                break;
            case Heading:
                stopHeading();
                break;
            case StraightProfile:
                stopStraightProfile();
                break;
            case RotationProfile:
                stopRotationProfile();
                break;
            case ArcProfile:
                stopArcProfile();
                break;
            case Segments:
                stopSegments();
                break;
        }
    }

    public boolean isStraightProfileOnTarget() {
        return straightMotionFollower.isOnTarget();
    }

    public boolean isRotationProfileOnTarget() {
        return rotationMotionFollower.isOnTarget();
    }

    public boolean isArcOnTarget() {
        return straightMotionFollower.isOnTarget() && rotationMotionFollower.isOnTarget();
    }

    public boolean isDriveSegmentsFollowerFinished() {
        return !driveSegmentsFollower.isRunning();
    }

    public TrapezoidalProfileFollower getStraightMotionFollower() {
        return straightMotionFollower;
    }

    public PidController getYawAngleController() {
        return yawAngleController;
    }

    public OutputController getOutputController() {
        return outputController;
    }

    public TrapezoidalProfileFollower getRotationMotionFollower() {
        return rotationMotionFollower;
    }

    public DriveSegmentsFollower getDriveSegmentsFollower() {
        return driveSegmentsFollower;
    }

    public void setRightPower(double power) {
        rightMotor.set(power);
    }

    public void setLeftPower(double power) {
        leftMotor.set(power);
    }

    public double getLeftRate() {
        return leftEncoder.getVelocity();
    }

    public double getRightRate() {
        return rightEncoder.getVelocity();
    }

    public double getLeftDistance() {
        return leftEncoder.getDistance();
    }

    public double getRightDistance() {
        return rightEncoder.getDistance();
    }

    public void resetDistances() {
        leftEncoder.reset();
        rightEncoder.reset();
    }

    public double getYawAngle() {
        return gyro.getAngle();
    }

    public void setYawAngle(double yawAngle) {
        gyro.setAngle(yawAngle);
    }

    public double getYawRate() {
        return gyro.getRate();
    }

    public void resetYawAngle() {
        gyro.resetAngle();
    }

    @Override
    public void setRightSpeed(double rightSpeed) {
        rightController.setSetpoint(rightSpeed);
    }

    @Override
    public void setLeftSpeed(double leftSpeed) {
        leftController.setSetpoint(leftSpeed);
    }

    @Override
    public TankKinematics getTankKinematics() {
        return tankKinematics;
    }

    public PoseEstimator getPoseEstimator() {
        return poseEstimator;
    }

    public Pose2d getPose() {
        return poseEstimator.getPose();
    }

    @Override
    public double getMaxSpeed() {
        return config.maxSpeed;
    }

    public List<Updatable> getUpdatables() {
        return Arrays.asList(
                gyro, poseEstimator, rotationController, yawAngleController,
                straightMotionFollower, driveSegmentsFollower, yawAngleController, leftController, rightController,
                rotationMotionFollower,
                outputController
        );
    }

    @Override
    public ManualTestGroup createManualTests() {
        ManualTestGroup tests = super.createManualTests();

        //tests.addTests(new SpeedControllerTest("LeftMotor", leftMotor));
        tests.addTests(new SRXEncoderTest("LeftEncoder", leftEncoder));
        //tests.addTests(new SpeedControllerTest("RightMotor", rightMotor));
        tests.addTests(new SRXEncoderTest("RightEncoder", rightEncoder));

        tests.addTests(new ADXRS453Test("gyro", gyro));

        tests.addTest(new CtreMotorControllerGroupTest("leftMotor", leftMotor));
        tests.addTest(new CtreMotorControllerGroupTest("rightMotor", rightMotor));

        tests.addTests(new ControllerTest(rotationController, 180));
        tests.addTests(new ControllerTest(leftController, config.maxSpeed));
        tests.addTests(new ControllerTest(rightController, config.maxSpeed));
        tests.addTests(new PoseEstimatorTest(poseEstimator));
        tests.addTest(new MotionCalibrationTest(straightMotionFollower) {
            @Override
            public void start() {
                super.start();
                outputController.start();
            }

            @Override
            public void stop() {
                super.stop();
                outputController.stop();
            }
        });

        return tests;
    }

    @Override
    public void onEnterRobotState(RobotState state) {
        stop();
        if (state == RobotState.TELEOP || state == RobotState.AUTONOMOUS || state == RobotState.TEST) {
            gyro.finishCalibration();
        }
        if (state == RobotState.AUTONOMOUS || state == RobotState.TELEOP) {
            gyro.resetAngle();
            poseEstimator.setPose(new Pose2d(Translation2d.zero(), Rotation.fromDegrees(90)));
            driveSegmentsFollower.reset();
        }
    }

    @Override
    public void configure(Config config) {
        super.configure();
        this.config = config;
        this.tankKinematics = config.tankKinematics;
        poseEstimator.setKinematics(tankKinematics);
        this.leftMotor = config.leftMotor.create();
        this.rightMotor = config.rightMotor.create();

        this.leftEncoder = new SRXEncoder((WPI_TalonSRX) leftMotor.getSpeedControllers()[0]);
        this.rightEncoder = new SRXEncoder((WPI_TalonSRX) rightMotor.getSpeedControllers()[0]);
        this.leftEncoder.configure(config.leftEncoder);
        this.rightEncoder.configure(config.rightEncoder);
        this.straightMotionFollower.configure(config.straightMotionFollower);
        this.yawAngleController.configure(config.yawAngleController);
        this.leftController.configure(config.speedController);
        this.rightController.configure(config.speedController);
        this.rotationMotionFollower.configure(config.rotationMotionFollower);
        this.driveSegmentsFollower.configure(config.driveSegmentsFollower);

        gyro = new ADXRS453(SPI.Port.kOnboardCS0);
        this.rotationController.configure(config.rotationController);
        gyro.start();
        gyro.startCalibration();
        //todo fix
        /*((Sendable) leftMotor).setName("Drive", "leftMotor");
        for (int i = 0; i < leftMotor.getSpeedControllers().length; i++) {
            SpeedController speedController = leftMotor.getSpeedControllers()[i];
            ((Sendable) speedController).setName("Drive", ("leftMotor(" + i + ")"));
        }
        ((Sendable) rightMotor).setName("Drive", "rightMotor");
        for (int i = 0; i < rightMotor.getSpeedControllers().length; i++) {
            SpeedController speedController = rightMotor.getSpeedControllers()[i];
            ((Sendable) speedController).setName("Drive", ("rightMotor(" + i + ")"));
        }*/
        //leftEncoder.setName("Drive", "leftEncoder");
        //rightEncoder.setName("Drive", "rightEncoder");
        gyro.setName("Drive", "gyro");

        poseEstimator.start();
    }

    @Override
    public void deconfigure() {
        super.deconfigure();
        this.config = null;
        SpeedControllerConfig.free(leftMotor);
        SpeedControllerConfig.free(rightMotor);
        //if (leftEncoder != null) leftEncoder.free();
        //if (rightEncoder != null) rightEncoder.free();
        if (gyro != null) gyro.free();
    }

    public enum DriveMode {
        Stop,
        Power,
        Speeds,
        Heading,
        StraightProfile,
        RotationProfile,
        ArcProfile,
        Segments
    }

    public static class Config {
        public CtreMotorControllerGroupConfig leftMotor;
        public CtreMotorControllerGroupConfig rightMotor;
        public SRXEncoder.Config leftEncoder;
        public SRXEncoder.Config rightEncoder;
        public PidController.Config rotationController;
        public TrapezoidalProfileFollower.Config straightMotionFollower;
        public PidController.Config speedController;
        public PidController.Config yawAngleController;
        public TrapezoidalProfileFollower.Config rotationMotionFollower;
        public TankKinematics tankKinematics;
        public DriveSegmentsFollower.Config driveSegmentsFollower;
        public double maxSpeed;
    }

    private class OutputController extends AbstractUpdatable {
        double leftOutput;
        double rightOutput;
        double straightOutput;
        boolean clearOutputs = true;
        double rotationOutput;

        OutputController() {
            super("Drive.OutputController");
        }

        void setLeftOutput(double leftOutput) {
            this.leftOutput = leftOutput;
        }

        void setRightOutput(double rightOutput) {
            this.rightOutput = rightOutput;
        }

        void setStraightOutput(double straightOutput, boolean clearOutputs) {
            this.straightOutput = straightOutput;
            this.clearOutputs = clearOutputs;
        }

        void setStraightOutput(double straightOutput) {
            setStraightOutput(straightOutput, true);
        }

        void setRotationOutput(double rotationOutput) {
            this.rotationOutput = rotationOutput;
        }

        @Override
        public synchronized void stop() {
            if (isRunning()) {
                setPowers(0, 0);
            }
            super.stop();
        }

        @Override
        protected void doUpdate(double delta) {
            double left = leftOutput;
            double right = rightOutput;
            leftOutput = 0;
            rightOutput = 0;
            left += straightOutput;
            right += straightOutput;
            if (clearOutputs) {
                straightOutput = 0;
            }
            left += rotationOutput;
            right -= rotationOutput;
            rotationOutput = 0;
            if (!Double.isNaN(left) && !Double.isNaN(right)) {
                setPowers(left, right);
            } else {
                setPowers(0, 0);
            }
        }
    }
}
