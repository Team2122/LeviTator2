package org.teamtators.common.harness.hw;

import org.teamtators.common.hw.AnalogInput;

public class HardwareAnalogInput extends edu.wpi.first.wpilibj.AnalogInput implements AnalogInput {
    public HardwareAnalogInput(int channel) {
        super(channel);
    }
}
