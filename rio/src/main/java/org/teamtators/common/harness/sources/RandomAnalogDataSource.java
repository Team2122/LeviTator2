package org.teamtators.common.harness.sources;

import org.teamtators.common.harness.HarnessAnalogDataSource;

public class RandomAnalogDataSource implements HarnessAnalogDataSource {
    @Override
    public double get() {
        return Math.random() * 5;
    }

    @Override
    public String getType() {
        return "Random";
    }

    @Override
    public void configure(String op, Object value) {

    }
}
