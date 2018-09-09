package org.teamtators.levitator2.subsystems;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SpeedController;
import org.teamtators.common.config.Configurable;
import org.teamtators.common.config.Deconfigurable;
import org.teamtators.common.config.helpers.EncoderConfig;
import org.teamtators.common.config.helpers.SpeedControllerConfig;
import org.teamtators.common.control.ControllerPredicates;
import org.teamtators.common.control.MotorPowerUpdater;
import org.teamtators.common.control.TrapezoidalProfileFollower;
import org.teamtators.common.scheduler.Subsystem;

public class Lift extends Subsystem implements Configurable<Lift.Config>, Deconfigurable {
    private TrapezoidalProfileFollower controller;
    private MotorPowerUpdater liftPowerUpdater;
    private SpeedController liftMotor;
    private Encoder liftEncoder;
    private double desiredHeight;
    private double heightToMoveTo; // D:
    private double maxHeight;
    private Config config;
    private boolean currentProfileLow;

    public Lift() {
        super("Lift");

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
        return liftEncoder.getRate();
    }

    private double getCurrentHeight() {
        return liftEncoder.getDistance();
    }

    public void setTargetHeight(double height) {
        double dist = height - getCurrentHeight();
        move(dist);
    }

    public void setDesiredHeight(double height) {
        if (height <= maxHeight && height >= 0) {
            this.desiredHeight = height;
        }
    }

    private void move(double dist) {
        controller.moveToPosition(dist);
    }

    public void checkProfiles() {
        if(getCurrentHeight() >= config.changeProfileHeight && currentProfileLow) {
            controller.stop();
            controller.configure(config.liftControllerHigh);
            currentProfileLow = false;
            setTargetHeight(heightToMoveTo); //assume it's still safe, no reason why it shouldn't be
        } else if(getCurrentHeight() < config.changeProfileHeight && !currentProfileLow) {
            controller.stop();
            controller.configure(config.liftControllerLow);
            currentProfileLow = true;
            setTargetHeight(heightToMoveTo); //assume it's still safe, no reason why it shouldn't be
        }
    }

    @Override
    public void configure(Config config) {
        liftMotor = config.liftMotor.create();
        liftEncoder = config.liftEncoder.create();
        controller.configure(config.liftControllerLow);
        currentProfileLow = true;
        liftPowerUpdater = new MotorPowerUpdater(liftMotor);
        this.maxHeight = config.maxHeight;

        this.config = config;
    }

    public void deconfigure() {
        liftEncoder.free();
        SpeedControllerConfig.free(liftMotor);
        liftPowerUpdater = null;
    }

    public class Config {
        public double maxHeight;
        public double changeProfileHeight;

        public SpeedControllerConfig liftMotor;
        public EncoderConfig liftEncoder;
        public TrapezoidalProfileFollower.Config liftControllerLow;
        public TrapezoidalProfileFollower.Config liftControllerHigh;
    }
}
