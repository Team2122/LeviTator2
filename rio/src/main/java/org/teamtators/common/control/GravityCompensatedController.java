package org.teamtators.common.control;

import java.util.function.DoubleSupplier;

public class GravityCompensatedController extends PidController {
    private DoubleSupplier angle;
    public GravityCompensatedController(String name, DoubleSupplier angle) {
        super(name);

        this.angle = angle;
    }

    public double computeFeedForward() {
        return 0;
    }
}
