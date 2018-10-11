package org.teamtators.levitator2.commands;

import org.teamtators.common.scheduler.Command;
import org.teamtators.levitator2.subsystems.Climber;

public class ClimberExtendForks extends Command {
    private final Climber climber;

    public ClimberExtendForks(Climber climber) {
        super("ClimberExtendForks");
        this.climber = climber;
    }

    @Override
    public boolean step() {
        climber.extendFork();
        return true;
    }
}
