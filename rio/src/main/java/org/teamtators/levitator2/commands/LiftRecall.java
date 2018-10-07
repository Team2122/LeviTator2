package org.teamtators.levitator2.commands;

import org.teamtators.common.scheduler.Command;
import org.teamtators.levitator2.TatorRobot;
import org.teamtators.levitator2.subsystems.Lift;

public class LiftRecall extends Command {
    private Lift lift;

    public LiftRecall(TatorRobot robot) {
        super("LiftRecall");
        this.lift = robot.getSubsystems().getLift();
    }

    @Override
    public void initialize() {
        logger.info("Clearing forced height flag");
        lift.clearForceHeightFlag();
//        lift.recallHeight();
    }

    @Override
    public void finish(boolean interrupted) {

    }

    @Override
    public boolean step() {
        return true;
    }
}
