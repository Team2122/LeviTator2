package org.teamtators.common.harness;

public interface HarnessAnalogDataSource {
    double get();
    String getType();
    void configure(String op, Object value);
}
