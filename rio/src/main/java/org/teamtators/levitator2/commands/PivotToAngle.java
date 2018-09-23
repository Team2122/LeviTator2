package org.teamtators.levitator2.commands;

import org.teamtators.common.config.Configurable;
import org.teamtators.common.scheduler.Command;
import org.teamtators.levitator2.subsystems.Pivot;

public class PivotToAngle extends Command implements Configurable<PivotToAngle.Config> {
    private final Pivot pivot;
    private Config config;

    public PivotToAngle(Pivot pivot) {
        super("PivotToAngle");
        this.pivot = pivot;
    }

    @Override
    public void configure(Config config) {
        this.config = config;
    }

    @Override
    protected void initialize() {
        super.initialize();
        logger.info("Moving to {}", config.angle);
        pivot.moveToAngle(config.angle);
    }

    @Override
    public boolean step() {
        return true;
    }

    public static class Config {
        public double angle;
    }
}
