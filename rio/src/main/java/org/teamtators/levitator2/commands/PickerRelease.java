package org.teamtators.levitator2.commands;

import org.teamtators.common.config.Configurable;
import org.teamtators.common.control.Timer;
import org.teamtators.common.scheduler.Command;
import org.teamtators.levitator2.TatorRobot;
import org.teamtators.levitator2.subsystems.Lift;
import org.teamtators.levitator2.subsystems.Picker;

public class PickerRelease extends Command implements Configurable<PickerRelease.Config> {
    private Picker picker;
    private Config config;
    private Timer timer;
    private Lift lift;

    public PickerRelease(TatorRobot robot) {
        super("PickerRelease");
        this.picker = robot.getSubsystems().getPicker();
        this.lift = robot.getSubsystems().getLift();
        timer = new Timer();
    }

    @Override
    protected void initialize() {
        super.initialize();
        if(lift.safeToReleaseCube()) {
            picker.setRollersPower(config.rollerPower, config.rollerPower);
            timer.start();
        } else {
           cancel();
        }
    }

    @Override
    public boolean step() {
        return timer.hasPeriodElapsed(config.time);
    }

    @Override
    protected void finish(boolean interrupted) {
        super.finish(interrupted);
        if(interrupted) {
            picker.setCubeState(Picker.CubeState.BAD_RELEASE);
        } else {
            picker.setCubeState(Picker.CubeState.SAFE_NOCUBE);
        }
        picker.stop();
        timer.stop();
    }

    public void configure(Config config) {
        this.config = config;
    }

    public static class Config {
        public double time;
        public double unsafeDistance;
        public double rollerPower;
    }
}
