package org.teamtators.common.hw;

import edu.wpi.first.wpilibj.AnalogInput;

public class PressureSensor {
    private AnalogInput pressureSensor;
    private double supplyVoltage;

    /**
     *
     * @param pressureSensor the input that represents the pressureSensor
     * @param supplyVoltage the supply voltage in VDC
     */
    public PressureSensor(AnalogInput pressureSensor, double supplyVoltage) {
        this.pressureSensor = pressureSensor;
        this.supplyVoltage = supplyVoltage;
    }

    /**
     *
     * @param supplyVoltage the supply voltage in VDC
     */
    public PressureSensor(int channel, double supplyVoltage) {
        this(new AnalogInput(channel), supplyVoltage);
    }

    public double getPressure(){
        double outputVoltage = getVoltage();
        return 250 * (outputVoltage/supplyVoltage) - 25;
    }

    public double getVoltage(){
        return pressureSensor.getVoltage();
    }
}
