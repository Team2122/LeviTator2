package org.teamtators.levitator2.commands;

import org.teamtators.common.scheduler.Command;
import org.teamtators.levitator2.TatorRobot;
import org.teamtators.levitator2.subsystems.Picker;

public class WaitForCube extends Command {
    private final Picker picker;

    public WaitForCube(TatorRobot robot) {
        super("WaitForCube");
        this.picker = robot.getSubsystems().getPicker();
    }

    @Override
    public boolean step() {
        return picker.getCubeState().hasCube();
    }
}
