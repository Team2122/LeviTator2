package org.teamtators.common.harness.sources;

import org.teamtators.common.harness.HarnessAnalogDataSource;

public class ConstantAnalogDataSource implements HarnessAnalogDataSource {
    private double value;

    @Override
    public double get() {
        return value;
    }

    @Override
    public String getType() {
        return "Constant";
    }

    @Override
    public void configure(String op, Object value) {
        switch (op) {
            case "set":
                this.value = (double) value;
        }
    }

    public void setValue(double value) {
        if (value < 0) {
            value = 0;
        }
        if (value > 5) {
            value = 5;
        }
        this.value = value;
    }
}
