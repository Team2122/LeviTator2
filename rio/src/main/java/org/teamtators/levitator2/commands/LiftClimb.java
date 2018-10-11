package org.teamtators.levitator2.commands;

import org.teamtators.common.config.Configurable;
import org.teamtators.common.control.Timer;
import org.teamtators.common.scheduler.Command;
import org.teamtators.levitator2.TatorRobot;
import org.teamtators.levitator2.subsystems.Lift;

public class LiftClimb extends Command implements Configurable<LiftClimb.Config> {
    private Config config;
    private Lift lift;
    private Timer shiftTimer;
    private boolean hasShifted;

    public LiftClimb(TatorRobot robot) {
        super("LiftClimb");
        this.lift = robot.getSubsystems().getLift();
        this.shiftTimer = new Timer();
    }

    @Override
    protected void initialize() {
        super.initialize();
        shiftTimer.start();
        lift.disable();
        lift.shift(Lift.Position.LOW);

    }

    @Override
    public void configure(Config config) {
        this.config = config;
    }

    @Override
    public boolean step() {
        if(shiftTimer.hasPeriodElapsed(config.time) || hasShifted) {
            if(!hasShifted) {
                hasShifted = true;
                logger.info("Shift state saved");
            }
            if(lift.getCurrentHeight() >= config.stopHeight) {
                lift.setPower(config.power);
            } else {
                logger.warn("STOP HEIGHT HIT!");
                lift.setPower(0);
            }
        }
        return false;
    }

    @Override
    protected void finish(boolean interrupted) {
        super.finish(interrupted);
        lift.setPower(0);
    }

    public static class Config {
        public double power;
        public double time;
        public double stopHeight;
    }
}
