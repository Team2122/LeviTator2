package org.teamtators.common.harness;

import org.teamtators.common.harness.hw.HardwareAnalogInput;
import org.teamtators.common.harness.hw.HarnessAnalogInput;
import org.teamtators.common.hw.AnalogInput;

public class HarnessHooks {
    private static HarnessContext context = null;
    public static AnalogInput getAnalogInput(String name, int channel, HarnessContext context) {
        if(context == null) {
            return new HardwareAnalogInput(channel);
        }
        if(!context.analogSources.containsKey(name)) {
            throw new RuntimeException(name + " is not providable by Harness. Please add an entry in the config.");
        }
        return new HarnessAnalogInput(context.analogSources.get(name));
    }

    public static HarnessContext getContext() {
        return context;
    }

    public static void setContext(HarnessContext context) {
        HarnessHooks.context = context;
    }
}
