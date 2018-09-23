package org.teamtators.common.hw;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import org.teamtators.common.config.Configurable;
import org.teamtators.common.config.Deconfigurable;

public class SRXEncoder implements Configurable<SRXEncoder.Config>, Deconfigurable {
    private WPI_TalonSRX motor;
    private double distancePerRotation;
    private boolean inverted;

    public SRXEncoder(WPI_TalonSRX motor) {
        this.motor = motor;
        reset(); //SRX encoder values persist between code restarts - we have to manually reset this here.
    }

    public double getDistance() {
        return (inverted ? -1 : 1) * (motor.getSelectedSensorPosition(0) / 4096.0) * distancePerRotation;
    }

    public double getVelocity() {
        return (inverted ? -1 : 1) * (motor.getSelectedSensorVelocity(0) / 4096.0) * distancePerRotation;
    }

    public void configure(Config config) {
        this.distancePerRotation = config.distancePerRotation;
        this.inverted = config.inverted;
    }

    @Override
    public void deconfigure() {

    }

    public void reset() {
        motor.setSelectedSensorPosition(0, 0, 0); //todo is 0ms okay?
    }

    public int get() {
        return motor.getSelectedSensorPosition(0);
    }

    public static class Config {
        public double distancePerRotation = 1.0;
        public boolean inverted = false;
    }
}
