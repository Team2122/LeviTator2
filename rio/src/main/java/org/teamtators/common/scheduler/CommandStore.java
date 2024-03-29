package org.teamtators.common.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class CommandStore {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private Map<String, Command> commands = new HashMap<>();

    public Map<String, Command> getCommands() {
        return commands;
    }

    public void putCommand(String name, Command command) {
        command.setName(name);
        commands.put(name, command);
    }

    public Command getCommand(String name) {
        Command command = commands.get(name);
        if (command == null)
            throw new IllegalArgumentException("No command with name \"" + name + "\" created");
        return command;
    }

    public void clearCommands() {
        commands.clear();
    }
}
