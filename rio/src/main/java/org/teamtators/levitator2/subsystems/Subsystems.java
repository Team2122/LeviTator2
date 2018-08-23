package org.teamtators.levitator2.subsystems;

import org.teamtators.common.SubsystemsBase;
import org.teamtators.common.config.ConfigLoader;
import org.teamtators.common.control.Updatable;
import org.teamtators.common.controllers.Controller;
import org.teamtators.common.controllers.LogitechF310;
import org.teamtators.common.scheduler.Subsystem;

import java.util.List;

public class Subsystems extends SubsystemsBase {


    @Override
    public List<Subsystem> getSubsystemList() {
        return null;
    }

    @Override
    public void configure(ConfigLoader configLoader) {

    }

    @Override
    public List<Updatable> getUpdatables() {
        return null;
    }

    @Override
    public List<Updatable> getMotorUpdatables() {
        return null;
    }

    @Override
    public List<Controller<?, ?>> getControllers() {
        return null;
    }

    @Override
    public LogitechF310 getTestModeController() {
        return null;
    }

    @Override
    public void deconfigure() {

    }
}
