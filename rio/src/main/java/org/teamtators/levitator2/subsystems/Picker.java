package org.teamtators.levitator2.subsystems;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.SpeedController;
import org.teamtators.common.config.Configurable;
import org.teamtators.common.config.helpers.DigitalSensorConfig;
import org.teamtators.common.config.helpers.DistanceLaserConfig;
import org.teamtators.common.config.helpers.SolenoidConfig;
import org.teamtators.common.config.helpers.SpeedControllerConfig;
import org.teamtators.common.hw.DigitalSensor;
import org.teamtators.common.hw.DistanceLaser;
import org.teamtators.common.scheduler.Subsystem;
import org.teamtators.common.tester.ManualTestGroup;
import org.teamtators.common.tester.components.DigitalSensorTest;
import org.teamtators.common.tester.components.DistanceLaserTest;
import org.teamtators.common.tester.components.SolenoidTest;
import org.teamtators.common.tester.components.SpeedControllerTest;

public class Picker extends Subsystem implements Configurable<Picker.Config> {
    private SpeedController leftRollers;
    private SpeedController rightRollers;
    private DigitalSensor cubeSensor;
    private DistanceLaser proximitySensor;
    private Solenoid pickSolenoid;
    private Solenoid releaseSolenoid;
    private Solenoid deathGripSolenoid;
    private CubeState state;


    public Picker() {
        super("Picker");
        state = CubeState.SAFE_NOCUBE;

    }

    public CubeState getCubeState() {
        return state;
    }

    public void setCubeState(CubeState state) {
        this.state = state;

        switch (state) {
            case BAD_PICK:
                logger.warn("!!!!! WARNING CUBE DETECTED BUT NOT IN PROPERLY!! RUN PICK AGAIN TO RESOLVE!! !!!!!");
                break;
            case BAD_RELEASE:
                logger.warn("!!!!! WARNING RELEASE INTERRUPTED!! RUN RELEASE AGAIN TO RESOLVE!! !!!!!");
                break;
            case SAFE_CUBE:
                logger.info("Safety flag cleared. Cube in picker.");
                break;
            case SAFE_NOCUBE:
                logger.info("Safety flag cleared. No cube in picker.");
                break;
        }
    }

    public void setMandibles(Position pos) {
        switch (pos) {
            case Pick:
                pickSolenoid.set(true);
                releaseSolenoid.set(false);
                break;
            case Close:
                pickSolenoid.set(false);
                releaseSolenoid.set(false);
                break;
            case Drop:
                pickSolenoid.set(false);
                releaseSolenoid.set(true);
                break;
        }
    }

    public boolean isCubeDetected() {
        return cubeSensor.get();
    }

    public void stop() {
        setRollersPower(0, 0);
    }

    public void setRollersPower(double leftPow, double rightPow) {
        setLeftRollerPower(leftPow);
        setRightRollerPower(rightPow);
    }

    public double getLaserDistance() {
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

        this.leftRollers = config.leftRollers.create();
        this.rightRollers = config.rightRollers.create();
        this.cubeSensor = config.cubeSensor.create();
        this.proximitySensor = config.proximitySensor.create();
        this.pickSolenoid = config.pickSolenoid.create();
        this.releaseSolenoid = config.releaseSolenoid.create();
        this.deathGripSolenoid = config.deathGripSolenoid.create();

    }

    @Override
    public ManualTestGroup createManualTests() {
        ManualTestGroup tests = super.createManualTests();
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
        public SpeedControllerConfig leftRollers;
        public SpeedControllerConfig rightRollers;
        public DigitalSensorConfig cubeSensor;
        public DistanceLaserConfig proximitySensor;
        public SolenoidConfig pickSolenoid;
        public SolenoidConfig releaseSolenoid;
        public SolenoidConfig deathGripSolenoid;
    }

    public enum CubeState {
        SAFE_NOCUBE(false),
        SAFE_CUBE(true),
        BAD_PICK(false),
        BAD_RELEASE(false);
        private final boolean cube;

        private CubeState(boolean cube) {
            this.cube = cube;
        }

        public boolean hasCube() {
            return cube;
        }
    }
}
