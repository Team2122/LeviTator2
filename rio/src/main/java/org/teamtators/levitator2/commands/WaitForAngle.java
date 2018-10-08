package org.teamtators.levitator2.commands;

import org.teamtators.common.scheduler.Command;
import org.teamtators.levitator2.TatorRobot;
import org.teamtators.levitator2.subsystems.Pivot;

public class WaitForAngle extends Command {
    private final Pivot pivot;

    public WaitForAngle(TatorRobot robot) {
        super("WaitForAngle");
        this.pivot = robot.getSubsystems().getPivot();
    }

    @Override
    public boolean step() {
        return pivot.isAtAngle();
    }
}
