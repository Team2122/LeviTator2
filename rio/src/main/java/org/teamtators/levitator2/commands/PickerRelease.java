package org.teamtators.levitator2.commands;

import org.teamtators.common.config.Configurable;
import org.teamtators.common.control.Timer;
import org.teamtators.common.scheduler.Command;
import org.teamtators.levitator2.TatorRobot;
import org.teamtators.levitator2.subsystems.Picker;
import org.teamtators.levitator2.subsystems.Pivot;

public class PickerRelease extends Command implements Configurable<PickerRelease.Config> {
    private final Pivot pivot;
    private Picker picker;
    private Config config;
    private Timer timer;

    public PickerRelease(TatorRobot robot) {
        super("PickerRelease");
        this.picker = robot.getSubsystems().getPicker();
        this.pivot = robot.getSubsystems().getPivot();
        requires(picker);
        timer = new Timer();
    }

    @Override
    protected void initialize() {
        super.initialize();
        picker.setRollersPower(config.rollerPower, config.rollerPower);
        if(config.drop && pivot.getCurrentAngle() > config.minDropAngle) {
            picker.setMandibles(Picker.Position.Drop);
        }
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
        picker.setMandibles(Picker.Position.Close);
        picker.stop();
        timer.stop();
    }

    public void configure(Config config) {
        this.config = config;
    }

    public static class Config {
        public double time;
        public double rollerPower;
        public boolean drop;
        public double minDropAngle;
    }
}
