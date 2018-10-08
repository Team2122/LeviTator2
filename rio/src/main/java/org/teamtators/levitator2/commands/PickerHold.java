package org.teamtators.levitator2.commands;

import org.teamtators.common.config.Configurable;
import org.teamtators.common.scheduler.Command;
import org.teamtators.levitator2.TatorRobot;
import org.teamtators.levitator2.subsystems.Picker;

public class PickerHold extends Command implements Configurable<PickerHold.Config> {
    private final Picker picker;
    private Config config;

    public PickerHold(TatorRobot robot) {
        super("PickerHold");
        this.picker = robot.getSubsystems().getPicker();
    }

    @Override
    public void configure(Config config) {
        this.config = config;
    }

    @Override
    protected void initialize() {
        super.initialize();
        picker.setRollersPower(config.power, config.power);
    }

    @Override
    public boolean step() {
        return true;
    }

    public static class Config {
        public double power;
    }
}
