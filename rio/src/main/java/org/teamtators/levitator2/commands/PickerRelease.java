package org.teamtators.levitator2.commands;

import org.teamtators.common.config.Configurable;
import org.teamtators.common.control.Timer;
import org.teamtators.common.scheduler.Command;
import org.teamtators.levitator2.subsystems.Picker;

public class PickerRelease extends Command implements Configurable<PickerRelease.Config> {
    private Picker picker;
    private Config config;
    private Timer timer;

    public PickerRelease(Picker picker) {
        super("PickerRelease");
        this.picker = picker;
        timer = new Timer();
    }

    @Override
    protected void initialize() {
        super.initialize();
        picker.setRollersPower(config.rollerPower, config.rollerPower);
        timer.start();
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
