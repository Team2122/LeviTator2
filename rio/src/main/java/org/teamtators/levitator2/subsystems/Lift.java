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
import org.teamtators.common.hw.CtreMotorControllerGroup;
import org.teamtators.common.hw.SRXEncoder;
import org.teamtators.common.hw.SpeedControllerGroup;
import org.teamtators.common.scheduler.Subsystem;
import org.teamtators.common.tester.ManualTestGroup;
import org.teamtators.common.tester.components.CtreMotorControllerGroupTest;
import org.teamtators.common.tester.components.DoubleSolenoidTest;
import org.teamtators.common.tester.components.SRXEncoderTest;
import org.teamtators.common.tester.components.SpeedControllerTest;

public class Lift extends Subsystem implements Configurable<Lift.Config>, Deconfigurable {
    private TrapezoidalProfileFollower controller;
    private CtreMotorControllerGroup liftMotor;
    private SRXEncoder liftEncoder;
    private Config config;
    private Picker picker;
    private DoubleSolenoid shifter;
    private boolean gear;

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
        //DANGER! Should be master when follower mode is enabled
        liftMotor.set(power);
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
    public ManualTestGroup createManualTests() {
        ManualTestGroup tests = super.createManualTests();
        //tests.addTest(new SpeedControllerTest("liftMotor", liftMotor));
        tests.addTest(new CtreMotorControllerGroupTest("liftMotor", liftMotor));
        tests.addTest(new SRXEncoderTest("liftEncoder", liftEncoder));
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

    public static class Config {
        public double maxHeight;

        public CtreMotorControllerGroupConfig liftMotor;
        public SRXEncoder.Config liftEncoder;
        public TrapezoidalProfileFollower.Config liftController;
        public DoubleSolenoidConfig shifter;
    }
}
