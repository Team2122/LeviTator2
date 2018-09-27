package org.teamtators.levitator2.commands;

import org.teamtators.common.config.ConfigCommandStore;
import org.teamtators.levitator2.TatorRobot;
import org.teamtators.levitator2.subsystems.Drive;
import org.teamtators.levitator2.subsystems.Lift;
import org.teamtators.levitator2.subsystems.Picker;
import org.teamtators.levitator2.subsystems.Pivot;

public class CommandRegistrar {
    private final TatorRobot robot;

    public CommandRegistrar(TatorRobot robot) {
        this.robot = robot;
    }

    public void register(ConfigCommandStore commandStore) {
        // Drive commands
        Drive drive = robot.getSubsystems().getDrive();
        commandStore.registerCommand("DriveTank", () -> new DriveTank(robot));
        commandStore.registerCommand("DriveStraight", () -> new DriveStraight(robot));
        commandStore.registerCommand("DriveRotate", () -> new DriveRotate(robot));

        Picker picker = robot.getSubsystems().getPicker();
        commandStore.registerCommand("PickerPick", () -> new PickerPick(picker));
        commandStore.registerCommand("PickerRelease", () -> new PickerRelease(robot));

        Pivot pivot = robot.getSubsystems().getPivot();
        commandStore.registerCommand("PivotToAngle", () -> new PivotToAngle(pivot));

        Lift lift = robot.getSubsystems().getLift();
        commandStore.registerCommand("LiftToHeight", () -> new LiftToHeight(lift));

    }

}
