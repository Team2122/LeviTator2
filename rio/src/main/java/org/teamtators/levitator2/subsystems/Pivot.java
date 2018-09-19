package org.teamtators.levitator2.subsystems;

import edu.wpi.first.wpilibj.SpeedController;
import org.teamtators.common.config.Configurable;
import org.teamtators.common.config.helpers.AnalogPotentiometerConfig;
import org.teamtators.common.config.helpers.SpeedControllerConfig;
import org.teamtators.common.control.PidController;
import org.teamtators.common.hw.AnalogPotentiometer;
import org.teamtators.common.scheduler.Subsystem;
import org.teamtators.common.tester.ManualTestGroup;
import org.teamtators.common.tester.components.AnalogPotentiometerTest;
import org.teamtators.common.tester.components.ControllerTest;
import org.teamtators.common.tester.components.SpeedControllerTest;

public class Pivot extends Subsystem implements Configurable<Pivot.Config> {
    private SpeedController motor;
    private AnalogPotentiometer position;
    private PidController pivotPositionController;

    public Pivot() {
        super("Pivot");
        pivotPositionController = new PidController("pivotPositionController");
        pivotPositionController.setInputProvider(this::getCurrentAngle);
        pivotPositionController.setOutputConsumer(this::setPower);
    }

    private void setPower(double power) {
        motor.set(power);
    }

    public void moveToAngle(double angle) {
        //todo safeties?
        pivotPositionController.setSetpoint(angle);
    }

    public double getCurrentAngle() {
        return position.get();
    }

    @Override
    public void configure(Config config) {
        this.motor = config.motor.create();
        this.position = config.position.create();
        this.pivotPositionController.configure(config.pivotPositionController);
    }

    public ManualTestGroup createManualTests() {
        ManualTestGroup tests = super.createManualTests();
        tests.addTest(new SpeedControllerTest("motor", motor));
        tests.addTest(new AnalogPotentiometerTest("position", position));
        tests.addTest(new ControllerTest(pivotPositionController, 0, 180));
        return tests;
    }

    public static class Config {
        public SpeedControllerConfig motor;
        public AnalogPotentiometerConfig position;
        public PidController.Config pivotPositionController;
    }
}