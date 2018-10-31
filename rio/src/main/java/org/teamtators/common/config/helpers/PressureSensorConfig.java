package org.teamtators.common.config.helpers;

import org.teamtators.common.harness.HarnessContext;
import org.teamtators.common.harness.HarnessHooks;
import org.teamtators.common.hw.PressureSensor;

public class PressureSensorConfig implements ConfigHelper<PressureSensor> {
    private int channel;
    private double supplyVoltage;

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public double getSupplyVoltage() {
        return supplyVoltage;
    }

    public void setSupplyVoltage(double supplyVoltage) {
        this.supplyVoltage = supplyVoltage;
    }

    public PressureSensor create(HarnessContext ctx) {
        PressureSensor sensor = ctx != null ? new PressureSensor(ctx) : new PressureSensor(channel);
        sensor.setSupplyVoltage(supplyVoltage);
        return sensor;
    }
}
