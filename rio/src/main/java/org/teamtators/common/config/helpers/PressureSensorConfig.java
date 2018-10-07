package org.teamtators.common.config.helpers;

import org.teamtators.common.hw.PressureSensor;

public class PressureSensorConfig {
    private int channel;
    private double supplyVoltage;

    public int getChannel(){ return channel; }

    public void setChannel(int channel) {this.channel = channel; }

    public double getSupplyVoltage(){ return supplyVoltage; }

    public void setSupplyVoltage(double supplyVoltage) {this.supplyVoltage = supplyVoltage; }

    public PressureSensor create(){ return new PressureSensor(channel, supplyVoltage);}
}
