package org.teamtators.common.hw;

import org.teamtators.common.harness.HarnessContext;
import org.teamtators.common.harness.HarnessHooks;
import org.teamtators.common.harness.HarnessTestable;

@HarnessTestable
public class PressureSensor {
    private AnalogInput pressureSensor;
    private double supplyVoltage;
    private String name;

    /**
     * @param pressureSensor the input that represents the pressureSensor
     */
    public PressureSensor(AnalogInput pressureSensor) {
        this.pressureSensor = pressureSensor;
        this.supplyVoltage = supplyVoltage;
    }

    /**
     * @param channel the input channel
     */
    public PressureSensor(int channel) {
        this(HarnessHooks.getAnalogInput(null, channel, null));
    }

    public PressureSensor(HarnessContext ctx) {
        pressureSensor = HarnessHooks.getAnalogInput(getName(), 255, ctx);
    }

    public double getPressure() {
        double outputVoltage = getVoltage();
        return 250 * (outputVoltage / supplyVoltage) - 25;
    }

    public double getVoltage() {
        return pressureSensor.getVoltage();
    }

    public void setSupplyVoltage(double v) {
        this.supplyVoltage = v;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
