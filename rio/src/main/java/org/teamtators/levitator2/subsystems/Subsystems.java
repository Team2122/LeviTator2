package org.teamtators.levitator2.subsystems;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.teamtators.common.SubsystemsBase;
import org.teamtators.common.config.ConfigException;
import org.teamtators.common.config.ConfigLoader;
import org.teamtators.common.config.Configurable;
import org.teamtators.common.config.Deconfigurable;
import org.teamtators.common.control.Updatable;
import org.teamtators.common.controllers.Controller;
import org.teamtators.common.controllers.LogitechF310;
import org.teamtators.common.scheduler.Subsystem;
import org.teamtators.levitator2.TatorRobot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Subsystems extends SubsystemsBase
        implements Configurable<Subsystems.Config>, Deconfigurable {
    private static final String SUBSYSTEMS_CONFIG_FILE = "Subsystems.yaml";
    private final List<Subsystem> subsystems;
    private final List<Updatable> updatables;
    private final List<Updatable> motorUpdatables;

    private OperatorInterface oi;
    private Drive drive;

    public Subsystems() {
        oi = new OperatorInterface();
        drive = new Drive();
        subsystems = Arrays.asList(oi, drive);

        updatables = new ArrayList<>();
        motorUpdatables = new ArrayList<>();
    }

    @Override
    public List<Subsystem> getSubsystemList() {
        return subsystems;
    }

    @Override
    public void configure(ConfigLoader configLoader) {
        try {
            ObjectNode configNode = (ObjectNode) configLoader.load(SUBSYSTEMS_CONFIG_FILE);
            Config configObj = configLoader.getObjectMapper().treeToValue(configNode, Config.class);
            configure(configObj);
        } catch (Throwable e) {
            throw new ConfigException("Error configuring subsystems: ", e);
        }
    }

    @Override
    public List<Updatable> getUpdatables() {
        return updatables;
    }

    @Override
    public List<Updatable> getMotorUpdatables() {
        return motorUpdatables;
    }

    @Override
    public List<Controller<?, ?>> getControllers() {
        return oi.getAllControllers();
    }

    @Override
    public LogitechF310 getTestModeController() {
        return oi.getDriverJoystick();
    }

    @Override
    public void deconfigure() {
        oi.deconfigure();
        drive.deconfigure();
    }

    @Override
    public void configure(Subsystems.Config config) {
        oi.configure(config.operatorInterface);
        drive.configure(config.drive);
    }

    public OperatorInterface getOperatorInterface() {
        return oi;
    }

    public Drive getDrive() {
        return drive;
    }


    public static class Config {
        public OperatorInterface.Config operatorInterface;
        public Drive.Config drive;
    }
}
