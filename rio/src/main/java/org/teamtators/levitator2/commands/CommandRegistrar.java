package org.teamtators.levitator2.commands;

import org.teamtators.common.TatorRobotBase;
import org.teamtators.common.config.ConfigCommandStore;
import org.teamtators.common.scheduler.Commands;
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
        commandStore.registerCommand("DrivePath", () -> new DrivePathCommand(robot));
        commandStore.putCommand("PrintPose", Commands.instant(() ->
                TatorRobotBase.logger.info("Pose: " + drive.getPose())));
        commandStore.registerCommand("SetPose", () -> new SetPose(robot));
        commandStore.registerCommand("WaitForPath", () -> new WaitForPath(robot));

        Picker picker = robot.getSubsystems().getPicker();
        commandStore.registerCommand("PickerPick", () -> new PickerPick(picker));
        commandStore.registerCommand("PickerRelease", () -> new PickerRelease(robot));
        commandStore.registerCommand("PickerHold", () -> new PickerHold(robot));
        commandStore.putCommand("WaitForCube", new WaitForCube(robot));

        Pivot pivot = robot.getSubsystems().getPivot();
        commandStore.registerCommand("PivotToAngle", () -> new PivotToAngle(pivot));
        commandStore.registerCommand("PivotFlip", () -> new PivotFlip(pivot));
        commandStore.putCommand("WaitForAngle", new WaitForAngle(robot));

        Lift lift = robot.getSubsystems().getLift();
        commandStore.registerCommand("LiftContinuous", () -> new LiftContinuous(robot));
        commandStore.registerCommand("LiftToHeight", () -> new LiftToHeight(lift));
        commandStore.registerCommand("LiftClimb", () -> new LiftClimb(robot));
        commandStore.putCommand("LiftRecall", new LiftRecall(robot));
        commandStore.putCommand("WaitForHeight", new WaitForHeight(robot));

        commandStore.registerCommand("AutoSelector", () -> new AutoSelector(robot));

    }

}
