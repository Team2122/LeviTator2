package org.teamtators.levitator2.subsystems;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import org.teamtators.common.config.Configurable;
import org.teamtators.common.config.Deconfigurable;
import org.teamtators.common.config.helpers.CtreMotorControllerGroupConfig;
import org.teamtators.common.config.helpers.DoubleSolenoidConfig;
import org.teamtators.common.config.helpers.SpeedControllerConfig;
import org.teamtators.common.control.TrapezoidalProfileFollower;
import org.teamtators.common.control.Updatable;
import org.teamtators.common.controllers.LogitechF310;
import org.teamtators.common.hw.CtreMotorControllerGroup;
import org.teamtators.common.hw.SRXEncoder;
import org.teamtators.common.scheduler.RobotState;
import org.teamtators.common.scheduler.Subsystem;
import org.teamtators.common.tester.ManualTest;
import org.teamtators.common.tester.ManualTestGroup;
import org.teamtators.common.tester.components.*;

import java.util.Arrays;
import java.util.List;

public class Lift extends Subsystem implements Configurable<Lift.Config>, Deconfigurable {
    private TrapezoidalProfileFollower controller;
    private CtreMotorControllerGroup liftMotor;
    private SRXEncoder liftEncoder;
    private Config config;
    private Picker picker;
    private DoubleSolenoid shifter;
    private Position gear;
    private double targetHeight = 0;
    private boolean forced = true;

    public Lift(Picker picker) {
        super("Lift");

        this.picker = picker;

        controller = new TrapezoidalProfileFollower("LiftController");
        controller.setPositionProvider(this::getCurrentHeight);
        controller.setVelocityProvider(this::getCurrentVelocity);
        controller.setOutputConsumer(this::setPower);
        controller.setOnTargetPredicate((follower) -> {
                    return targetHeight == 0 && (getCurrentHeight() <= 0.25);
                }
        );
        //controller.setOnTargetPredicate(ControllerPredicates.alwaysFalse());
    }

    public void setPower(double power) {
        liftMotor.set(power);
    }

    private double getCurrentVelocity() {
        return liftEncoder.getVelocity();
    }

    public double getCurrentHeight() {
        return liftEncoder.getDistance();
    }

    public boolean safeToMoveTo(double height) {
        Picker.CubeState state = picker.getCubeState();
        switch (state) {
            case SAFE_NOCUBE:
            case SAFE_CUBE:
                return true;
            case BAD_PICK:
                return height < config.maxBadPickHeight;
            case BAD_RELEASE:
                return height > config.minBadReleaseHeight;
        }
        //java sucks
        return false;
    }

    public boolean safeToReleaseCube() {
        return config.minUnsafeReleaseHeight > getCurrentHeight() || config.maxUnsafeReleaseHeight < getCurrentHeight();
    }

    public void setTargetHeight(double height, boolean forced) {
        if(forced || !this.forced && safeToMoveTo(height)) {
            this.targetHeight = height;
            double dist = height - getCurrentHeight();
            moveTo(dist);
            enableLiftController();
            if(forced) {
                this.forced = true;
            }
        }
    }


    private void moveTo(double dist) {
        controller.moveDistance(dist);
    }

    @Override
    public void configure(Config config) {
        liftMotor = config.liftMotor.create(ctx);
        liftEncoder = new SRXEncoder(liftMotor.getMaster());
        liftEncoder.configure(config.liftEncoder);
        controller.configure(config.liftController);
        shifter = config.shifter.create(ctx);

        this.config = config;
    }

    public void deconfigure() {
        //liftEncoder.free();
        SpeedControllerConfig.free(liftMotor);
        shifter.free();
    }

    @Override
    public void onEnterRobotState(RobotState state) {
        super.onEnterRobotState(state);
        switch (state) {
            case TELEOP:
            case AUTONOMOUS:
                enableLiftController();
                setTargetHeight(targetHeight, true);
                shift(Position.HIGH);
                break;
            case TEST:
            case DISABLED:
                disableLiftController();
                break;
        }
    }

    @Override
    public ManualTestGroup createManualTests() {
        ManualTestGroup tests = super.createManualTests();
        tests.addTest(new CtreMotorControllerGroupTest("liftMotor", liftMotor));
        tests.addTest(new SRXEncoderTest("liftEncoder", liftEncoder));
        tests.addTest(new DoubleSolenoidTest("liftShifter", shifter));
        tests.addTest(new LiftTest());
        return tests;
    }

    public void shift(Position pos) {
        this.gear = pos;
        switch (pos) {
            case HIGH:
                shifter.set(DoubleSolenoid.Value.kReverse);
                break;
            case LOW:
                shifter.set(DoubleSolenoid.Value.kForward);
                break;
        }
    }

    public boolean isAtHeight() {
        return Math.abs(getCurrentHeight() - getTargetHeight()) < config.heightTolerance;
    }

    public double getTargetHeight() {
        return targetHeight;
    }

    public boolean isHeightForced() {
        return forced;
    }

    public void clearForceHeightFlag() {
        forced = false;
    }

    public enum Position {
        HIGH,
        LOW
    }

    public void disable() {
        controller.stop();
    }

    public void enable() {
        controller.start();
    }

    public List<Updatable> getUpdatables() {
        return Arrays.asList(controller);
    }

    public static class Config {
        public double maxHeight;

        public CtreMotorControllerGroupConfig liftMotor;
        public SRXEncoder.Config liftEncoder;
        public TrapezoidalProfileFollower.Config liftController;
        public DoubleSolenoidConfig shifter;
        public double maxBadPickHeight;
        public double minBadReleaseHeight;
        public double minUnsafeReleaseHeight;
        public double maxUnsafeReleaseHeight;
        public double heightTolerance;
    }

    private class LiftTest extends ManualTest {
        private double axisValue;

        public LiftTest() {
            super("LiftTest");
        }

        @Override
        public void start() {
            logger.info("Press A to set lift target to joystick value. Hold Y to enable lift profiler");
            disableLiftController();
        }

        @Override
        public void onButtonDown(LogitechF310.Button button) {
            switch (button) {
                case A:
                    double height = (config.maxHeight)
                            * ((axisValue + 1) / 2);
                    setTargetHeight(height, true);
                    break;
                case Y:
                    enableLiftController();
                    break;
            }
        }

        @Override
        public void onButtonUp(LogitechF310.Button button) {
            switch (button) {
                case Y:
                    disableLiftController();
                    break;
            }
        }

        @Override
        public void updateAxis(double value) {
            this.axisValue = value;
        }

        @Override
        public void stop() {
            disableLiftController();
        }
    }

    private void disableLiftController() {
        controller.stop();
    }

    private void enableLiftController() {
        controller.start();
    }
}
