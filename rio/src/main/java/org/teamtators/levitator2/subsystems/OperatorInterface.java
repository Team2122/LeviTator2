package org.teamtators.levitator2.subsystems;

import org.teamtators.common.config.Configurable;
import org.teamtators.common.controllers.ButtonBoardFingers;
import org.teamtators.common.controllers.Controller;
import org.teamtators.common.controllers.LogitechF310;
import org.teamtators.common.controllers.RawController;
import org.teamtators.common.math.LinearInterpolationFunction;
import org.teamtators.common.scheduler.Subsystem;
import org.teamtators.common.tester.ManualTest;
import org.teamtators.common.tester.ManualTestGroup;

import java.util.Arrays;
import java.util.List;

public class OperatorInterface extends Subsystem implements Configurable<OperatorInterface.Config> {
    private LogitechF310 driverJoystick = new LogitechF310("driver");
    private LogitechF310 gunnerJoystick = new LogitechF310("gunner");
    private List<Controller<?, ?>> controllers;
    private Config config;

    public OperatorInterface() {
        super("Operator Interface");
    }

    // For tank drive
    public double getDriveLeft() {
        return -driverJoystick.getAxisValue(LogitechF310.Axis.LEFT_STICK_Y);
    }

    public double getDriveRight() {
        return -driverJoystick.getAxisValue(LogitechF310.Axis.RIGHT_STICK_Y);
    }

    @Override
    public void configure(Config config) {
        super.configure();
        this.config = config;
        driverJoystick.configure(config.driverJoystick);
        //gunnerJoystick.configure(config.gunnerJoystick);

        controllers = Arrays.asList(
                driverJoystick//,
                //gunnerJoystick
        );
    }

    public LogitechF310 getDriverJoystick() {
        return driverJoystick;
    }

    public LogitechF310 getGunnerJoystick() {
        return gunnerJoystick;
    }

    public List<Controller<?, ?>> getAllControllers() {
        return controllers;
    }

    @Override
    public ManualTestGroup createManualTests() {
        ManualTestGroup group = super.createManualTests();
        group.addTest(new OITest());
        return group;
    }

    public static class Config {
        public LogitechF310.Config driverJoystick;
        public LogitechF310.Config gunnerJoystick;
    }

    private class OITest extends ManualTest {
        public OITest() {
            super("OITest");
        }

        @Override
        public void start() {
            printTestInstructions("Press A to get statuses");
        }

        @Override
        public void onButtonDown(LogitechF310.Button button) {
            printTestInfo("Tank: Left = {}, Right = {}", getDriveLeft(), getDriveRight());
        }
    }
}