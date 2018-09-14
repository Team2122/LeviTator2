package org.teamtators.levitator2.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.SpeedController;
import org.teamtators.common.config.Configurable;
import org.teamtators.common.config.Deconfigurable;
import org.teamtators.common.config.helpers.SpeedControllerConfig;
import org.teamtators.common.config.helpers.SpeedControllerGroupConfig;
import org.teamtators.common.control.ControllerPredicates;
import org.teamtators.common.control.MotorPowerUpdater;
import org.teamtators.common.control.TrapezoidalProfileFollower;
import org.teamtators.common.hw.SRXEncoder;
import org.teamtators.common.hw.SpeedControllerGroup;
import org.teamtators.common.scheduler.Subsystem;
import org.teamtators.common.tester.ManualTestGroup;
import org.teamtators.common.tester.components.SRXEncoderTest;
import org.teamtators.common.tester.components.SpeedControllerTest;

public class Lift extends Subsystem implements Configurable<Lift.Config>, Deconfigurable {
    private TrapezoidalProfileFollower controller;
    private MotorPowerUpdater liftPowerUpdater;
    private SpeedControllerGroup liftMotor;
    private WPI_TalonSRX liftMaster;
    private SRXEncoder liftEncoder;
    private Config config;
    private Picker picker;

    public Lift(Picker picker) {
        super("Lift");

        this.picker = picker;

        controller = new TrapezoidalProfileFollower("LiftController");
        controller.setPositionProvider(this::getCurrentHeight);
        controller.setVelocityProvider(this::getCurrentVelocity);
        controller.setOutputConsumer(this::setPower);
        controller.setOnTargetPredicate(ControllerPredicates.alwaysFalse());
    }

    private void setPower(double power) {
        liftPowerUpdater.set(power);
    }

    private double getCurrentVelocity() {
        return liftEncoder.getVelocity();
    }

    private double getCurrentHeight() {
        return liftEncoder.getDistance();
    }

    public void setTargetHeight(double height) {
        double dist = height - getCurrentHeight();
        move(dist);
    }


    private void move(double dist) {
        controller.moveToPosition(dist);
    }

    @Override
    public void configure(Config config) {
        liftMotor = config.liftMotor.create();
        liftMaster = (WPI_TalonSRX) liftMotor.getSpeedControllers()[0];
        liftEncoder = new SRXEncoder(liftMaster);
        liftEncoder.configure(config.liftEncoder);
        controller.configure(config.liftController);
        liftPowerUpdater = new MotorPowerUpdater(liftMaster);

        this.config = config;
    }

    public void deconfigure() {
        //liftEncoder.free();
        SpeedControllerConfig.free(liftMotor);
        liftPowerUpdater = null;
    }

    @Override
    public ManualTestGroup createManualTests() {
        ManualTestGroup tests = super.createManualTests();
        tests.addTest(new SpeedControllerTest("liftMotor", liftMaster));
        tests.addTest(new SRXEncoderTest("liftEncoder", liftEncoder));


        for (int i = 0; i < liftMotor.getSpeedControllers().length; i++) {
            SpeedController speedController = liftMotor.getSpeedControllers()[i];
            tests.addTest(new SpeedControllerTest("liftMotor(" + i + ")", speedController));
        }

        return tests;
    }

    public static class Config {
        public double maxHeight;

        public SpeedControllerGroupConfig liftMotor;
        public SRXEncoder.Config liftEncoder;
        public TrapezoidalProfileFollower.Config liftController;
    }
}
