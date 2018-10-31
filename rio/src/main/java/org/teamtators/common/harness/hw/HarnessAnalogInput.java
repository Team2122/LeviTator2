package org.teamtators.common.harness.hw;

import org.teamtators.common.harness.HarnessAnalogDataSource;
import org.teamtators.common.hw.AnalogInput;

public class HarnessAnalogInput implements AnalogInput {
    private HarnessAnalogDataSource sauce;
    private int avgBits = 1;
    private boolean freed;
    private int oversample = 1;

    public HarnessAnalogInput(HarnessAnalogDataSource harnessAnalogDataSource) {
        this.sauce = harnessAnalogDataSource;
    }

    @Override
    public double getVoltage() {
        checkFreed();
        return sauce.get();
    }

    @Override
    public double getAverageVoltage() {
        checkFreed();
        return getVoltage(); //sorry
    }

    @Override
    public void free() {
        this.freed = true;
    }

    @Override
    public void setAverageBits(int averageBits) {
        checkFreed();
        this.avgBits = averageBits;

    }

    @Override
    public int getAverageBits() {
        checkFreed();
        return avgBits;
    }

    @Override
    public void setOversampleBits(int oversampleBits) {
        checkFreed();
        this.oversample = oversampleBits;
    }

    @Override
    public int getOversampleBits() {
        checkFreed();
        return oversample;
    }


    private void checkFreed() {
        if (freed) {
            throw new RuntimeException("The sensor was accessed after being freed!");
        }
    }
}
