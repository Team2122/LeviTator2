package org.teamtators.levitator2.commands;

import org.teamtators.common.config.Configurable;
import org.teamtators.common.scheduler.Command;
import org.teamtators.levitator2.subsystems.Lift;

public class LiftToHeight extends Command implements Configurable<LiftToHeight.Config> {
    private final Lift lift;
    private Config config;

    public LiftToHeight(Lift lift) {
        super("LiftToHeight");
        this.lift = lift;
    }

    @Override
    public void configure(Config config) {
        this.config = config;
    }

    @Override
    protected void initialize() {
        super.initialize();
        logger.info("Moving to {}", config.height);
        //lift.setDesiredHeight(config.height);
        lift.setTargetHeight(config.height);
    }

    @Override
    public boolean step() {
        return true;
    }

    public static class Config {
        public double height;
    }
}
