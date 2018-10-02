package org.teamtators.levitator2.commands;

import org.teamtators.common.config.Configurable;
import org.teamtators.common.scheduler.Command;
import org.teamtators.levitator2.subsystems.Pivot;

public class PivotFlip extends Command implements Configurable<PivotFlip.Config> {
    private final Pivot pivot;
    private Config config;

    public PivotFlip(Pivot pivot) {
        super("PivotFlip");
        this.pivot = pivot;
    }

    @Override
    protected void initialize() {
        super.initialize();
        double moveTo = config.forwardAngle;
        if (pivot.getCurrentAngle() >= config.pivotPoint) {
            moveTo = config.backwardAngle;
        }
        logger.info("Flipping to {}", moveTo);
        pivot.moveToAngle(moveTo);
    }

    @Override
    public boolean step() {
        return true;
    }

    @Override
    public void configure(Config config) {
        this.config = config;
    }

    public static class Config {
        public double forwardAngle;
        public double pivotPoint;
        public double backwardAngle;
    }
}
