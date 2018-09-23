package org.teamtators.common.control;

import java.util.function.DoubleSupplier;

public class GravityCompensatedController extends PidController {
    private DoubleSupplier angle;
    private PidController.Config config;

    public GravityCompensatedController(String name, DoubleSupplier angle) {
        super(name);

        this.angle = angle;
    }

    public double computeFeedForward() {
        if(!isOnTarget() && false) {
            double angle = this.angle.getAsDouble();
            angle = 90 - angle;
            double comp = config.gravityComp * -Math.sin(angle);
            logger.info("Power compensation: {}", comp);
            return comp;
        } else {
            return 0;
        }
    }

    public void configure(PidController.Config config) {
        super.configure(config);
        this.config = config;
    }
}
