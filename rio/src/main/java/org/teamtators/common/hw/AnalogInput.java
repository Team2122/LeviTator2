package org.teamtators.common.hw;

public interface AnalogInput {
    double getVoltage();
    double getAverageVoltage();
    void free();
    void setAverageBits(int averageBits);
    int getAverageBits();
    void setOversampleBits(int oversampleBits);
    int getOversampleBits();
}
