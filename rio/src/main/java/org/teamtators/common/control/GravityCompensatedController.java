package org.teamtators.common.control;

import org.teamtators.common.datalogging.LogDataProvider;

import java.util.Arrays;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

public class GravityCompensatedController extends AbstractController {
    private DoubleSupplier angle;
    private BooleanSupplier select;
    private Config config;
    private LogDataProvider provider;
    private double comp;
    private double kMin;
    private double output;


    public GravityCompensatedController(String name, DoubleSupplier angle, BooleanSupplier select) {
        super(name);

        this.angle = angle;
        this.select = select;
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
        double kMin = 0;
        double kComp = 0;
        if (isOnTarget()) {
            kMin = 0;
            comp = 0;
            output = 0;
            return 0;
        }
        if (select.getAsBoolean()) {
            kMin = config.kMinB;
            kComp = config.kCompB;
        } else {
            kMin = config.kMinA;
            kComp = config.kCompA;
        }
        double angle = this.angle.getAsDouble();
        angle = 90 - angle;
        kMin = kMin * (getError() > 0 ? 1 : -1);
        comp = kComp * Math.sin(angle * (Math.PI / 180.0)) + kMin;
        this.comp = comp - kMin;
        this.output = comp;
        this.kMin = kMin;
        return comp;
    }

    public static class Config extends AbstractController.Config {
        public double kCompA;
        public double kMinA;
        public double kCompB;
        public double kMinB;
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
