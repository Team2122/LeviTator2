package org.teamtators.levitator2.commands;

import org.teamtators.common.config.Configurable;
import org.teamtators.common.scheduler.Command;
import org.teamtators.common.scheduler.RobotState;
import org.teamtators.levitator2.TatorRobot;
import org.teamtators.levitator2.subsystems.Lift;
import org.teamtators.levitator2.subsystems.OperatorInterface;

public class LiftContinuous extends Command implements Configurable<LiftContinuous.Config> {
    private Lift lift;
    private OperatorInterface oi;
    private TatorRobot robot;
    private LiftContinuous.Config config;

    public LiftContinuous(TatorRobot robot) {
        super("LiftContinuous");
        this.lift = robot.getSubsystems().getLift();
        this.oi = robot.getSubsystems().getOperatorInterface();
        this.robot = robot;
        requires(lift);
    }

    @Override
    protected void initialize() {
        super.initialize();
        lift.enable();
    }

    @Override
    public boolean step() {
        boolean isTeleop = robot.getState() == RobotState.TELEOP;
        if(isTeleop) {
            updateSlider(oi.getSliderHeight());
        }
        return false;
    }

    private void updateSlider(double sliderHeight) {
        boolean atHeight = lift.isAtHeight();

        double heightDelta = Math.abs(lift.getTargetHeight() - sliderHeight);
        if (atHeight && lift.isHeightForced()) {
            if (heightDelta < config.sliderTolerance) {
                lift.clearForceHeightFlag();
            }
        }

        boolean allowSlider = !lift.isHeightForced();
        if (allowSlider && heightDelta > config.sliderThreshold) {
            logger.info("Moving to {}", sliderHeight);
            lift.setTargetHeight(sliderHeight, false);
        }
    }

    @Override
    public void configure(Config config) {
        this.config = config;
    }

    public static class Config {
        public double sliderTolerance;
        public double sliderThreshold;
    }
}
