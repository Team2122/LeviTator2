package org.teamtators.levitator2.subsystems;

import edu.wpi.first.wpilibj.SpeedController;
import org.teamtators.common.config.Configurable;
import org.teamtators.common.config.helpers.AnalogPotentiometerConfig;
import org.teamtators.common.config.helpers.PressureSensorConfig;
import org.teamtators.common.config.helpers.SpeedControllerConfig;
import org.teamtators.common.control.ControllerPredicates;
import org.teamtators.common.control.GravityCompensatedController;
import org.teamtators.common.control.PidController;
import org.teamtators.common.control.Updatable;
import org.teamtators.common.hw.AnalogPotentiometer;
import org.teamtators.common.hw.PressureSensor;
import org.teamtators.common.scheduler.RobotState;
import org.teamtators.common.scheduler.Subsystem;
import org.teamtators.common.tester.ManualTestGroup;
import org.teamtators.common.tester.components.AnalogPotentiometerTest;
import org.teamtators.common.tester.components.ControllerTest;
import org.teamtators.common.tester.components.PressureSensorTest;
import org.teamtators.common.tester.components.SpeedControllerTest;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Pivot extends Subsystem implements Configurable<Pivot.Config> {
    private SpeedController motor;
    private AnalogPotentiometer position;
    private GravityCompensatedController pivotPositionController;
    private PressureSensor pressureSensor;
    private Config config;
    private double targetAngle;

    public Pivot() {
        super("Pivot");
        pivotPositionController = new GravityCompensatedController("pivotPositionController", this::getCurrentAngle);
        pivotPositionController.setInputProvider(this::getCurrentAngle);
        pivotPositionController.setOutputConsumer(this::setPower);
        pivotPositionController.setTargetPredicate(ControllerPredicates.withinError(2));
    }

    private void setPower(double power) {
        motor.set(power);
    }

    public void moveToAngle(double angle) {
        if(pressureSensor.getPressure() > config.dangerPressure){

            //todo safeties?
            this.targetAngle = angle;
            pivotPositionController.setSetpoint(angle);
        } else {
            logger.warn("Below danger pressure! Moving to safe angle!");
            if(angle < 90) {
                angle = 90;
            }
            this.targetAngle = angle;
            pivotPositionController.setSetpoint(angle);
        }

    }

    public double getCurrentAngle() {
        return position.get();
    }

    @Override
    public void configure(Config config) {
        this.motor = config.motor.create();
        this.position = config.position.create();
        this.pressureSensor = config.pressureSensor.create();
        this.pivotPositionController.configure(config.pivotPositionController);
        this.config = config;
    }

    @Override
    public void onEnterRobotState(RobotState state) {
        super.onEnterRobotState(state);
        switch (state) {
            case TELEOP:
            case AUTONOMOUS:
                pivotPositionController.start();
                break;
            case DISABLED:
            case TEST:
                pivotPositionController.stop();
        }
    }

    public ManualTestGroup createManualTests() {
        ManualTestGroup tests = super.createManualTests();
        tests.addTest(new SpeedControllerTest("motor", motor));
        tests.addTest(new AnalogPotentiometerTest("position", position));
        tests.addTest(new ControllerTest(pivotPositionController, 0, 180));
        tests.addTest(new PressureSensorTest("pivotPressure", pressureSensor));
        return tests;
    }

    public List<Updatable> getUpdatables() {
        return Arrays.asList(pivotPositionController);
    }

    public boolean isAtAngle() {
        return Math.abs(targetAngle - getCurrentAngle()) <= config.angleTolerance;
    }

    public static class Config {
        public SpeedControllerConfig motor;
        public AnalogPotentiometerConfig position;
        public GravityCompensatedController.Config pivotPositionController;
        public PressureSensorConfig pressureSensor;
        public double dangerPressure;
        public double angleTolerance;
    }
}
