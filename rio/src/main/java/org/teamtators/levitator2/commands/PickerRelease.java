package org.teamtators.levitator2.commands;

import org.teamtators.common.config.Configurable;
import org.teamtators.common.scheduler.Command;
import org.teamtators.levitator2.subsystems.Picker;

public class PickerRelease extends Command implements Configurable<PickerRelease.Config> {
    private Picker picker;
    private Config config;

    public PickerRelease(Picker picker) {
        super("PickerRelease");
        this.picker = picker;
    }

    @Override
    protected void initialize() {
        super.initialize();
        picker.setRollersPower(config.rollerPower, config.rollerPower);
    }

    @Override
    public boolean step() {
        return picker.getLaserDistance() > config.finishDistance;
    }

    @Override
    protected void finish(boolean interrupted) {
        super.finish(interrupted);
        picker.stop();
    }

    public void configure(Config config) {
        this.config = config;
    }

    public static class Config {
        public double finishDistance;
        public double rollerPower;
    }
}
