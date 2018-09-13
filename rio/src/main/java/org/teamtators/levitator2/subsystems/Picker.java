package org.teamtators.levitator2.subsystems;

import edu.wpi.first.wpilibj.SpeedController;
import org.teamtators.common.config.Configurable;
import org.teamtators.common.config.helpers.AnalogPotentiometerConfig;
import org.teamtators.common.config.helpers.DigitalSensorConfig;
import org.teamtators.common.config.helpers.SpeedControllerConfig;
import org.teamtators.common.control.PidController;
import org.teamtators.common.hw.AnalogPotentiometer;
import org.teamtators.common.hw.DigitalSensor;
import org.teamtators.common.scheduler.Subsystem;

public class Picker extends Subsystem implements Configurable<Picker.Config> {
    private SpeedController pivot;
    private SpeedController leftRollers;
    private SpeedController rightRollers;
    private DigitalSensor cubeSensor;
    private DigitalSensor mandiblesClosed;
    private AnalogPotentiometer pivotPosition;

    private PidController pivotPositionController;

    public Picker() {
        super("Picker");
        pivotPositionController = new PidController("pivotPositionController");
        pivotPositionController.setInputProvider(this::getCurrentAngle);
        pivotPositionController.setOutputConsumer(this::setPivotPower);
    }

    private void setPivotPower(double power) {
        pivot.set(power);
    }

    public void moveToAngle(double angle) {
        //todo safeties?
        pivotPositionController.setSetpoint(angle);
    }

    public double getCurrentAngle() {
        return -1.0;
    }

    public void setMandibles(Position pos) {

    }

    public boolean isClosed() {
        return mandiblesClosed.get();
    }

    public boolean isCubeDetected() {
        return cubeSensor.get();
    }

    public void stop() {
        pivotPositionController.stop();
        setPivotPower(0);
        setRollersPower(0, 0);
    }

    public void setRollersPower(double leftPow, double rightPow) {
        setLeftRollerPower(leftPow);
        setRightRollerPower(rightPow);
    }

    public void setLeftRollerPower(double pow) {
        leftRollers.set(pow);
    }

    public void setRightRollerPower(double pow) {
        rightRollers.set(pow);
    }

    public enum Position {
        Pick,
        Close,
        Drop
    }

    public void configure(Config config) {
        this.pivot = config.pivot.create();
        this.leftRollers = config.leftRollers.create();
        this.rightRollers = config.rightRollers.create();
        this.cubeSensor = config.cubeSensor.create();
        this.mandiblesClosed = config.mandiblesClosed.create();
        this.pivotPosition = config.pivotPosition.create();
        this.pivotPositionController.configure(config.pivotPositionController);
    }


    public class Config {
        public SpeedControllerConfig pivot;
        public SpeedControllerConfig leftRollers;
        public SpeedControllerConfig rightRollers;
        public DigitalSensorConfig cubeSensor;
        public DigitalSensorConfig mandiblesClosed;
        public AnalogPotentiometerConfig pivotPosition;

        public PidController.Config pivotPositionController;
    }

}
