package org.teamtators.common.hw;

import edu.wpi.first.wpilibj.RobotController;
import org.teamtators.common.harness.HarnessHooks;

public class DistanceLaser {
    private double distance0V;
    private double distance5V;

    private AnalogInput distanceLaser;

    /**
     * @param distanceLaser the input that represents this distanceLaser
     */
    public DistanceLaser(AnalogInput distanceLaser) {
        this.distanceLaser = distanceLaser;
    }

    /**
     * @param channel the channel of the input that represents this distanceLaser
     */
    public DistanceLaser(int channel) {
        this(HarnessHooks.getAnalogInput(null, channel, null));
    }

    public double getDistance() {
        //todo simulate (or return 5) getVoltage5V
        double prop = distanceLaser.getVoltage() / RobotController.getVoltage5V();
        return (prop * (distance5V - distance0V)) + distance0V;
    }

    public double getVoltage() {
        return distanceLaser.getVoltage();
    }

    public double getDistance0V() {
        return distance0V;
    }

    public double getDistance5V() {
        return distance5V;
    }

    AnalogInput getAnalogInput() {
        return distanceLaser;
    }

    public void setDistance0V(double distance0V) {
        this.distance0V = distance0V;
    }

    public void setDistance5V(double distance5V) {
        this.distance5V = distance5V;
    }
}
