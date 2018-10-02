package org.teamtators.levitator2.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.SpeedController;
import org.teamtators.common.config.Configurable;
import org.teamtators.common.config.Deconfigurable;
import org.teamtators.common.config.helpers.CtreMotorControllerGroupConfig;
import org.teamtators.common.config.helpers.DoubleSolenoidConfig;
import org.teamtators.common.config.helpers.SpeedControllerConfig;
import org.teamtators.common.config.helpers.SpeedControllerGroupConfig;
import org.teamtators.common.control.ControllerPredicates;
import org.teamtators.common.control.TrapezoidalProfileFollower;
import org.teamtators.common.control.Updatable;
import org.teamtators.common.controllers.LogitechF310;
import org.teamtators.common.hw.CtreMotorControllerGroup;
import org.teamtators.common.hw.SRXEncoder;
import org.teamtators.common.hw.SpeedControllerGroup;
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
    private boolean gear;
    private double desiredHeight;

    public Lift(Picker picker) {
        super("Lift");

        this.picker = picker;

        controller = new TrapezoidalProfileFollower("LiftController");
        controller.setPositionProvider(this::getCurrentHeight);
        controller.setVelocityProvider(this::getCurrentVelocity);
        controller.setOutputConsumer(this::setPower);
        controller.setOnTargetPredicate((follower) -> {
                    return desiredHeight == 0 && (getCurrentHeight() <= 2.0);
                }
        );
        //controller.setOnTargetPredicate(ControllerPredicates.alwaysFalse());
    }

    private void setPower(double power) {
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

    public void setTargetHeight(double height) {
        this.desiredHeight = height;
        double dist = height - getCurrentHeight();
        move(dist);
        enableLiftController();
    }

    public void setDesiredHeight(double height) {
        if (height <= config.maxHeight && height >= 0.0)
            this.desiredHeight = height;
    }


    private void move(double dist) {
        controller.moveToPosition(dist);
    }

    @Override
    public void configure(Config config) {
        liftMotor = config.liftMotor.create();
        liftEncoder = new SRXEncoder(liftMotor.getMaster());
        liftEncoder.configure(config.liftEncoder);
        controller.configure(config.liftController);
        shifter = config.shifter.create();

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
        tests.addTest(new LiftTest());
        return tests;
    }

    public void shift(boolean high) {
        this.gear = high;
        if (high) {
            shifter.set(DoubleSolenoid.Value.kReverse);
        } else {
            shifter.set(DoubleSolenoid.Value.kForward);
        }
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
                    setTargetHeight(height);
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
