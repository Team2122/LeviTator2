package org.teamtators.levitator2.commands;

import org.teamtators.common.scheduler.Command;
import org.teamtators.levitator2.subsystems.Climber;

public class ClimberExtendHook extends Command {
    private final Climber climber;

    public ClimberExtendHook(Climber climber) {
        super("ClimberExtendHook");
        this.climber = climber;
    }

    @Override
    public boolean step() {
        climber.extendHook();
        return true;
    }
}
