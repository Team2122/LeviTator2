package org.teamtators.common.control;

import org.teamtators.common.datalogging.LogDataProvider;

import java.util.Arrays;
import java.util.List;
import java.util.function.DoubleSupplier;

public class GravityCompensatedController extends AbstractController {
    private DoubleSupplier angle;
    private Config config;
    private LogDataProvider provider;
    private double comp;
    private double kMin;
    private double output;

    public GravityCompensatedController(String name, DoubleSupplier angle) {
        super(name);

        this.angle = angle;
        this.provider = new GravityCompensatedControllerDataProvider();
    }

    @Override
    public LogDataProvider getLogDataProvider() {
        return provider;
    }

    public void configure(Config config) {
        super.configure(config);
        this.config = config;
    }

    @Override
    protected double computeOutput(double delta) {
        if (isOnTarget()) {
            kMin = 0;
            comp = 0;
            output = 0;
            return 0;
        }
        double angle = this.angle.getAsDouble();
        angle = 90 - angle;
        kMin = config.kMin * (getError() > 0 ? 1 : -1);
        double comp = config.kComp * Math.sin(angle * (Math.PI / 180.0)) + kMin;
        this.comp = comp - kMin;
        this.output = comp;
        return comp;
    }

    public static class Config extends AbstractController.Config {
        public double kComp;
        public double kMin;
    }

    private class GravityCompensatedControllerDataProvider implements LogDataProvider {

        @Override
        public String getName() {
            return GravityCompensatedController.this.getName();
        }

        @Override
        public List<Object> getKeys() {
            return Arrays.asList("setpoint", "kMin", "comp", "output");
        }

        @Override
        public List<Object> getValues() {
            synchronized (GravityCompensatedController.this) {
                return Arrays.asList(getSetpoint(), kMin, comp, output);
            }
        }
    }
}
