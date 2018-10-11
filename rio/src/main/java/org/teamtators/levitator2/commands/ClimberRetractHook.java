package org.teamtators.levitator2.commands;

import org.teamtators.common.scheduler.Command;
import org.teamtators.levitator2.subsystems.Climber;

public class ClimberRetractHook extends Command {
    private final Climber climber;

    public ClimberRetractHook(Climber climber) {
        super("ClimberRetractHook");
        this.climber = climber;
    }

    @Override
    public boolean step() {
        climber.retractHook();
        return true;
    }
}
