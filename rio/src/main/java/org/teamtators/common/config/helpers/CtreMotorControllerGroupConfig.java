package org.teamtators.common.config.helpers;

import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import org.teamtators.common.harness.HarnessContext;
import org.teamtators.common.hw.CtreMotorControllerGroup;

import java.util.ArrayList;

public class CtreMotorControllerGroupConfig extends ArrayList<SpeedControllerConfig>
        implements ConfigHelper<CtreMotorControllerGroup> {

    public CtreMotorControllerGroup create(HarnessContext ctx) {
        BaseMotorController[] controllers = new BaseMotorController[this.size()];
        for (int i = 0; i < controllers.length; i++) {
            controllers[i] = (BaseMotorController) this.get(i).create(ctx);
        }
        return new CtreMotorControllerGroup(controllers);
    }
}
