package org.teamtators.common.hw;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.interfaces.Potentiometer;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;

public class AnalogPotentiometer extends SensorBase implements Potentiometer, Sendable {
    public static final double DEFAULT_FULL_RANGE = 360.0;
    public static final double DEFAULT_OFFSET = 0.0;
    public static final boolean DEFAULT_CONTINUOUS = false;
    private AnalogInput analogInput;
    private double fullRange = DEFAULT_FULL_RANGE;
    private double offset = DEFAULT_OFFSET;
    private double minValue = 0.0;
    private boolean continuous = DEFAULT_CONTINUOUS;
    private boolean inverted = false;

    public AnalogPotentiometer(int channel) {
        analogInput = new AnalogInput(channel);

        addChild(analogInput);
    }

    public double getRawVoltage() {
        return analogInput.getVoltage();
    }

    public void free() {
        super.free();
        analogInput.free();
    }

    @Override
    public double get() {
        double p = analogInput.getAverageVoltage() / RobotController.getVoltage5V();
        double value = p * fullRange;
        double absFullRange = Math.abs(value);
        if (inverted) {
            value = fullRange - value;
        }
        value += offset;
        if (continuous) {
            while (value < minValue) {
                value += absFullRange;
            }
            while (value > (minValue + fullRange)) {
                value -= absFullRange;
            }
        }
        return value;
    }

    public double getFullRange() {
        return fullRange;
    }

    public void setFullRange(double fullRange) {
        this.fullRange = fullRange;
    }

    public double getOffset() {
        return offset;
    }

    public void setOffset(double offset) {
        this.offset = offset;
    }

    public boolean isContinuous() {
        return continuous;
    }

    public void setContinuous(boolean continuous) {
        this.continuous = continuous;
    }

    public double getMinValue() {
        return minValue;
    }

    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }

    public void setAverageBits(int averageBits) {
        analogInput.setAverageBits(averageBits);
    }

    public int getAverageBits() {
        return analogInput.getAverageBits();
    }

    public void setOversampleBits(int oversampleBits) {
        analogInput.setOversampleBits(oversampleBits);
    }

    public int getOversampleBits() {
        return analogInput.getOversampleBits();
    }

    @Override
    public PIDSourceType getPIDSourceType() {
        return PIDSourceType.kDisplacement;
    }

    @Override
    public void setPIDSourceType(PIDSourceType pidSource) {
        if (pidSource != PIDSourceType.kDisplacement) {
            throw new IllegalArgumentException("Only supports displacement");
        }
    }

    @Override
    public double pidGet() {
        return this.get();
    }

    public AnalogInput getAnalogInput() {
        return analogInput;
    }

    @Override
    public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("Analog Input");
        builder.addDoubleProperty("Value", this::get, null);
        builder.addDoubleProperty("Offset", this::getOffset, this::setOffset);
    }

    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }
}
