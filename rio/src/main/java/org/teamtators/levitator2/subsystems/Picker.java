package org.teamtators.levitator2.subsystems;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.SpeedController;
import org.teamtators.common.config.Configurable;
import org.teamtators.common.config.helpers.*;
import org.teamtators.common.control.PidController;
import org.teamtators.common.hw.AnalogPotentiometer;
import org.teamtators.common.hw.DigitalSensor;
import org.teamtators.common.hw.DistanceLaser;
import org.teamtators.common.scheduler.Subsystem;
import org.teamtators.common.tester.ManualTestGroup;
import org.teamtators.common.tester.components.*;

public class Picker extends Subsystem implements Configurable<Picker.Config> {
    private SpeedController pivot;
    private SpeedController leftRollers;
    private SpeedController rightRollers;
    private DigitalSensor cubeSensor;
    private AnalogPotentiometer pivotPosition;
    private DistanceLaser proximitySensor;
    private Solenoid pickSolenoid;
    private Solenoid releaseSolenoid;
    private Solenoid deathGripSolenoid;
    private CubeState state;

    private PidController pivotPositionController;

    public Picker() {
        super("Picker");
        pivotPositionController = new PidController("pivotPositionController");
        pivotPositionController.setInputProvider(this::getCurrentAngle);
        pivotPositionController.setOutputConsumer(this::setPivotPower);
        state = CubeState.SAFE;

    }

    public CubeState getCubeState(){
        return state;
    }

    public void setCubeState(CubeState state){
        this.state = state;
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

    public double getLaserDistance(){
        return proximitySensor.getDistance();
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
        this.pivotPosition = config.pivotPosition.create();
        this.pivotPositionController.configure(config.pivotPositionController);
        this.proximitySensor = config.proximitySensor.create();
        this.pickSolenoid = config.pickSolenoid.create();
        this.releaseSolenoid = config.releaseSolenoid.create();
        this.deathGripSolenoid = config.deathGripSolenoid.create();

    }

    @Override
    public ManualTestGroup createManualTests() {
        ManualTestGroup tests = super.createManualTests();
        tests.addTest(new SpeedControllerTest("pivot", pivot));
        tests.addTest(new AnalogPotentiometerTest("pivotPosition", pivotPosition));
        tests.addTest(new SpeedControllerTest("rightRoller", rightRollers));
        tests.addTest(new SpeedControllerTest("leftRoller", leftRollers));
        tests.addTest(new DigitalSensorTest("cubeSensor", cubeSensor));
        tests.addTest(new DistanceLaserTest("proximitySensor", proximitySensor));
        tests.addTest(new SolenoidTest("pickSolenoid", pickSolenoid));
        tests.addTest(new SolenoidTest("releaseSolenoid", releaseSolenoid));
        tests.addTest(new SolenoidTest("deathGripSolenoid", deathGripSolenoid));
        return tests;
    }

    public static class Config {
        public SpeedControllerConfig pivot;
        public SpeedControllerConfig leftRollers;
        public SpeedControllerConfig rightRollers;
        public DigitalSensorConfig cubeSensor;
        public AnalogPotentiometerConfig pivotPosition;
        public DistanceLaserConfig proximitySensor;

        public SolenoidConfig pickSolenoid;
        public SolenoidConfig releaseSolenoid;
        public SolenoidConfig deathGripSolenoid;

        public PidController.Config pivotPositionController;
    }

    public enum CubeState{
        SAFE,
        BAD_PICK,
        BAD_RELEASE
    }
}
