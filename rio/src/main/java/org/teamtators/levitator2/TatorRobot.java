package org.teamtators.levitator2;

import org.slf4j.profiler.Profiler;
import org.teamtators.common.SubsystemsBase;
import org.teamtators.common.TatorRobotBase;
import org.teamtators.common.config.ConfigCommandStore;
import org.teamtators.common.scheduler.Command;
import org.teamtators.levitator2.commands.CommandRegistrar;
import org.teamtators.levitator2.subsystems.Subsystems;

public class TatorRobot extends TatorRobotBase {
    private Subsystems subsystems;
    private final CommandRegistrar registrar = new CommandRegistrar(this);

    public TatorRobot(String configDir) {
        super(configDir);

        subsystems = new Subsystems();
    }

    @Override
    public SubsystemsBase getSubsystemsBase() {
        return subsystems;
    }

    @Override
    public String getName() {
        return "LeviTator2";
    }

    @Override
    public void setProfiler(Profiler profiler) {

    }

    @Override
    protected void registerCommands(ConfigCommandStore commandStore) {
        super.registerCommands(commandStore);
        registrar.register(commandStore);
    }

    @Override
    public boolean hasProfiler() {
        return false;
    }

    public Subsystems getSubsystems() {
        return subsystems;
    }

    @Override
    protected Command getAutoCommand() {
        return getCommandStore().getCommand("$CenterLSwitchAuto");
    }
}
