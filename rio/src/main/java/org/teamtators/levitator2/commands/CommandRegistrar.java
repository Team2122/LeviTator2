package org.teamtators.levitator2.commands;

import org.teamtators.common.config.ConfigCommandStore;
import org.teamtators.levitator2.TatorRobot;
import org.teamtators.levitator2.subsystems.Drive;

public class CommandRegistrar {
    private final TatorRobot robot;

    public CommandRegistrar(TatorRobot robot) {
        this.robot = robot;
    }

    public void register(ConfigCommandStore commandStore) {
        // Drive commands
        Drive drive = robot.getSubsystems().getDrive();
        commandStore.registerCommand("DriveTank", () -> new DriveTank(robot));

    }

}