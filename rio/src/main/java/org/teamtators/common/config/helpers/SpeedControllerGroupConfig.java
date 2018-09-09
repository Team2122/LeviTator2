package org.teamtators.common.config.helpers;

import edu.wpi.first.wpilibj.SpeedController;
import org.teamtators.common.hw.SpeedControllerGroup;
import com.ctre.phoenix.motorcontrol.IMotorController;

import java.util.ArrayList;

public class SpeedControllerGroupConfig extends ArrayList<SpeedControllerConfig>
        implements ConfigHelper<SpeedControllerGroup> {
    public boolean followMaster = true;

    public SpeedControllerGroup create() {
        SpeedController[] controllers = new SpeedController[this.size()];
        for (int i = 0; i < controllers.length; i++) {
            controllers[i] = this.get(i).create();

            if(followMaster && i > 0 && controllers[i] instanceof IMotorController) {
                ((IMotorController) controllers[i]).follow((IMotorController) controllers[0]);
            }
        }
        return new SpeedControllerGroup(controllers);
    }
}
