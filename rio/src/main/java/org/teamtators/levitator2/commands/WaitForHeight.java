package org.teamtators.levitator2.commands;

import org.teamtators.common.scheduler.Command;
import org.teamtators.levitator2.TatorRobot;
import org.teamtators.levitator2.subsystems.Lift;

public class WaitForHeight extends Command {
    private final Lift lift;

    public WaitForHeight(TatorRobot robot) {
        super("WaitForHeight");
        this.lift = robot.getSubsystems().getLift();
    }

    @Override
    public boolean step() {
        return lift.isAtHeight();
    }
}
